#!/usr/bin/env bash
# Verifies the running Vinheria Agnello infrastructure environment.
# Prints one PASS or FAIL line per check, then a summary.
# Exits non-zero when any check fails.

set -u

readonly CONTAINER_DNSMASQ=agnello-dnsmasq
readonly CONTAINER_PROBE=agnello-probe
readonly CONTAINER_CATALOG=agnello-catalog
readonly CONTAINER_ORDER=agnello-order
readonly NETWORK=agnello-net
readonly DNS_SERVER=172.28.0.2
readonly ZONE=agnello.internal
# Dynamic pool handed out by dnsmasq (172.28.0.100 to .199) and where it keeps its lease records.
readonly POOL_PREFIX=172.28.0.
readonly POOL_FIRST=100
readonly POOL_LAST=199
readonly LEASE_FILE=/var/lib/misc/dnsmasq.leases
# Client containers reuse the dnsmasq image for its busybox nslookup and wget.
readonly CLIENT_IMAGE=agnello-dnsmasq
# name:address of every host with a fixed address.
readonly FIXED_HOSTS="catalog:172.28.0.11 order:172.28.0.12"
# The Order Service's address (it is the one that looks the Catalog Service up) and the two ports.
readonly ORDER_ADDRESS=172.28.0.12
readonly CATALOG_PORT=8081
readonly ORDER_PORT=8082
# Seed data: Catalog Service wine 1, and Carrinho 1 of the Order Service:
# 2 x Cabernet Sauvignon Reserva at 89.90 and 1 x Chardonnay Premium at 79.90, total 259.70 (worked out by hand).
readonly SEED_WINE="Cabernet Sauvignon Reserva"
readonly SEED_CART_WINE_2="Chardonnay Premium"
readonly SEED_CART_TOTAL="259.70"
# No --wait: Compose fails it when the one-shot probe exits, even with status 0.
# The probe starts only after the other containers are healthy, so plain `up -d` is enough.
readonly START_HINT="from the repository root run: docker compose -f infra/docker-compose.yml up -d --build"

passed=0
failed=0

pass() {
  printf 'PASS  %s\n' "$1"
  passed=$((passed + 1))
}

fail() {
  printf 'FAIL  %s\n' "$1"
  if [ $# -gt 1 ]; then
    printf '      %s\n' "$2"
  fi
  failed=$((failed + 1))
}

# Prints "healthy", "unhealthy", "starting", "no-healthcheck", "not-running",
# "not-found" or "docker-unavailable".
container_state() {
  local state
  if state=$(docker inspect -f '{{if .State.Running}}{{if .State.Health}}{{.State.Health.Status}}{{else}}no-healthcheck{{end}}{{else}}not-running{{end}}' "$1" 2>/dev/null); then
    echo "$state"
  elif docker info >/dev/null 2>&1; then
    echo not-found
  else
    echo docker-unavailable
  fi
}

check_healthy() {
  local description=$1 container=$2 state=$3 detail
  if [ "$state" = healthy ]; then
    pass "$description"
    return
  fi
  case $state in
    docker-unavailable) detail="cannot reach the Docker daemon (is Docker running?)" ;;
    not-found | not-running) detail="container $container is $state; $START_HINT" ;;
    *) detail="container $container is $state" ;;
  esac
  fail "$description" "$detail"
}

# Fails the check and returns non-zero when a container is not healthy:
# call as `require_healthy "$description" "$dnsmasq_state" dnsmasq || return`.
require_healthy() {
  [ "$2" = healthy ] && return 0
  fail "$1" "not run: $3 is not healthy"
  return 1
}

# Asks dnsmasq directly, from a throwaway client container on the private
# network. Prints nslookup's output; returns nslookup's exit status.
dns_query() {
  local type=$1 name=$2
  docker run --rm --network "$NETWORK" --entrypoint nslookup "$CLIENT_IMAGE" \
    "-type=$type" "$name" "$DNS_SERVER" 2>&1
}

# Last non-empty line of a command's output, for failure details.
last_line() {
  grep -v '^$' | tail -n 1
}

check_resolves() {
  local host=$1 expected=$2
  local description="dns: $host.$ZONE resolves to $expected"
  local answer
  require_healthy "$description" "$dnsmasq_state" dnsmasq || return
  answer=$(dns_query A "$host.$ZONE")
  if printf '%s\n' "$answer" | grep -qFx "Address: $expected"; then
    pass "$description"
  else
    fail "$description" "answer was: $(printf '%s\n' "$answer" | last_line)"
  fi
}

in_pool() {
  local address=$1 last_octet=${1##*.}
  case $address in
    "$POOL_PREFIX"*) ;;
    *) return 1 ;;
  esac
  [ "$last_octet" -ge "$POOL_FIRST" ] && [ "$last_octet" -le "$POOL_LAST" ]
}

# Re-runs the one-shot probe and checks every claim about its DHCP handshake.
# The probe's own output proves the client side (discover, request, acknowledge
# from dnsmasq); dnsmasq's DHCP log proves the offer, which udhcpc does not print.
check_dhcp() {
  local description="dhcp: probe completes discover, offer, request and acknowledge, gets a pool address and registers in DNS"
  local mac log_lines_before probe_output dnsmasq_log leased server answer dhcp_message expected_address
  require_healthy "$description" "$dnsmasq_state" dnsmasq || return
  if ! docker inspect "$CONTAINER_PROBE" >/dev/null 2>&1; then
    fail "$description" "container $CONTAINER_PROBE not found; $START_HINT"
    return
  fi

  # The startup run may still be going; starting a running container only attaches to it.
  docker wait "$CONTAINER_PROBE" >/dev/null 2>&1
  log_lines_before=$(docker logs "$CONTAINER_DNSMASQ" 2>&1 | wc -l)
  if ! probe_output=$(docker start -a "$CONTAINER_PROBE" 2>&1); then
    fail "$description" "probe exited with an error: $(printf '%s\n' "$probe_output" | last_line)"
    return
  fi
  dnsmasq_log=$(docker logs "$CONTAINER_DNSMASQ" 2>&1 | tail -n +"$((log_lines_before + 1))")

  mac=$(printf '%s\n' "$probe_output" | sed -n 's/^probe: MAC address \([0-9a-f:]*\)$/\1/p' | head -n 1)
  if [ -z "$mac" ]; then
    fail "$description" "probe output does not report its MAC address"
    return
  fi

  read -r leased server <<EOF
$(printf '%s\n' "$probe_output" | sed -n 's/.*lease of \([0-9.]*\) obtained from \([0-9.]*\).*/\1 \2/p' | tail -n 1)
EOF
  if [ -z "$leased" ] || [ "$server" != "$DNS_SERVER" ]; then
    fail "$description" "probe output shows no lease obtained from $DNS_SERVER; last line: $(printf '%s\n' "$probe_output" | last_line)"
    return
  fi
  if ! printf '%s\n' "$probe_output" | grep -q 'broadcasting discover'; then
    fail "$description" "probe output shows no discover"
    return
  fi
  if ! printf '%s\n' "$probe_output" | grep -qF "select for $leased, server $DNS_SERVER"; then
    fail "$description" "probe output shows no request for $leased to $DNS_SERVER"
    return
  fi
  for dhcp_message in DHCPDISCOVER DHCPOFFER DHCPREQUEST DHCPACK; do
    # A discover carries no address yet; an empty pattern matches every line.
    expected_address=$leased
    [ "$dhcp_message" = DHCPDISCOVER ] && expected_address=
    if ! printf '%s\n' "$dnsmasq_log" | grep -F "$dhcp_message" | grep -F "$mac" | grep -qF "$expected_address"; then
      fail "$description" "dnsmasq log shows no $dhcp_message for $mac${expected_address:+ and $expected_address}"
      return
    fi
  done
  if ! in_pool "$leased"; then
    fail "$description" "leased address $leased is outside the pool $POOL_PREFIX$POOL_FIRST-$POOL_LAST"
    return
  fi
  if ! docker exec "$CONTAINER_DNSMASQ" cat "$LEASE_FILE" 2>/dev/null | awk -v mac="$mac" -v ip="$leased" '$2 == mac && $3 == ip && $4 == "probe" { found = 1 } END { exit !found }'; then
    fail "$description" "lease file $LEASE_FILE does not list $leased for $mac as probe"
    return
  fi
  answer=$(dns_query A "probe.$ZONE")
  if printf '%s\n' "$answer" | grep -qFx "Address: $leased"; then
    pass "$description"
  else
    fail "$description" "probe.$ZONE did not resolve to $leased; answer was: $(printf '%s\n' "$answer" | last_line)"
  fi
}

# Fetches a URL from a throwaway client container on the private network that
# uses dnsmasq as its resolver. Prints the body; returns the client's exit status.
http_get_from_network() {
  docker run --rm --network "$NETWORK" --dns "$DNS_SERVER" --entrypoint wget "$CLIENT_IMAGE" \
    -q -O- -T 10 "$1" 2>&1
}

# Beginning of a response body, for failure details (a wine list is long).
response_excerpt() {
  printf '%s\n' "$1" | last_line | cut -c1-200
}

check_catalog_list_by_dns_name() {
  local url="http://catalog.$ZONE:$CATALOG_PORT/v1/vinhos"
  local description="catalog: wine list is returned through catalog.$ZONE:$CATALOG_PORT from a client container on $NETWORK"
  local body
  require_healthy "$description" "$dnsmasq_state" dnsmasq || return
  require_healthy "$description" "$catalog_state" "Catalog Service" || return
  if body=$(http_get_from_network "$url") && printf '%s\n' "$body" | grep -qF "\"nome\":\"$SEED_WINE\""; then
    pass "$description"
  else
    fail "$description" "GET $url did not return the seeded wine; response was: $(response_excerpt "$body")"
  fi
}

# Succeeds when a response body is the seeded Carrinho 1 with its items and total.
is_seeded_cart() {
  printf '%s\n' "$1" | grep -qF '"idCarrinho":1' &&
    printf '%s\n' "$1" | grep -qF "\"nomeVinho\":\"$SEED_WINE\"" &&
    printf '%s\n' "$1" | grep -qF "\"nomeVinho\":\"$SEED_CART_WINE_2\"" &&
    printf '%s\n' "$1" | grep -qF "\"total\":$SEED_CART_TOTAL"
}

# The Order Service stores only wine ids and quantities: the names and the total can only
# come from a call to the Catalog Service, by DNS name.
check_order_cart_by_dns_name() {
  local url="http://order.$ZONE:$ORDER_PORT/v1/carrinhos/1"
  local description="order: Carrinho 1 is returned with the names and prices of the Catalog Service and total $SEED_CART_TOTAL through order.$ZONE:$ORDER_PORT"
  local body
  require_healthy "$description" "$dnsmasq_state" dnsmasq || return
  require_healthy "$description" "$order_state" "Order Service" || return
  if body=$(http_get_from_network "$url") && is_seeded_cart "$body"; then
    pass "$description"
  else
    fail "$description" "GET $url did not return the seeded Carrinho 1; response was: $(response_excerpt "$body")"
  fi
}

# The Order Service resolves catalog.agnello.internal when it asks the Catalog Service for a wine,
# so the lookup is in dnsmasq's log once the Carrinho has been read.
check_order_resolved_catalog_through_dnsmasq() {
  local description="dns: dnsmasq's query log shows catalog.$ZONE looked up from the Order Service ($ORDER_ADDRESS)"
  require_healthy "$description" "$dnsmasq_state" dnsmasq || return
  if docker logs "$CONTAINER_DNSMASQ" 2>&1 | grep -qF "query[A] catalog.$ZONE from $ORDER_ADDRESS"; then
    pass "$description"
  else
    fail "$description" "no such lookup in the dnsmasq log; if dnsmasq was recreated, restart the Order Service (docker restart $CONTAINER_ORDER) and run this again"
  fi
}

dnsmasq_state=$(container_state "$CONTAINER_DNSMASQ")
catalog_state=$(container_state "$CONTAINER_CATALOG")
order_state=$(container_state "$CONTAINER_ORDER")
check_healthy "startup: dnsmasq is healthy" "$CONTAINER_DNSMASQ" "$dnsmasq_state"
check_healthy "startup: Catalog Service is healthy" "$CONTAINER_CATALOG" "$catalog_state"
check_healthy "startup: Order Service is healthy" "$CONTAINER_ORDER" "$order_state"

for fixed_host in $FIXED_HOSTS; do
  check_resolves "${fixed_host%%:*}" "${fixed_host#*:}"
done
check_dhcp
check_catalog_list_by_dns_name
check_order_cart_by_dns_name
check_order_resolved_catalog_through_dnsmasq

printf '\nSummary: %d PASS, %d FAIL\n' "$passed" "$failed"
[ "$failed" -eq 0 ]

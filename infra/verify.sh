#!/usr/bin/env bash
# Verifies the running Vinheria Agnello infrastructure environment: DNS, DHCP, TLS and JWT.
# Prints one PASS or FAIL line per check, then a summary.
# Exits non-zero when any check fails.

set -u
# Git Bash on Windows would rewrite container paths such as /run/agnello into Windows paths.
export MSYS_NO_PATHCONV=1

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
# HTTP(S) client containers use the netlab image (curl, tcpdump, tshark), built from the tools profile.
readonly NETLAB_IMAGE=agnello-netlab
COMPOSE_FILE="$(cd "$(dirname "$0")" && pwd)/docker-compose.yml"
# With MSYS_NO_PATHCONV, host paths handed to docker are converted here instead (C:/... on Git Bash).
command -v cygpath >/dev/null 2>&1 && COMPOSE_FILE=$(cygpath -m "$COMPOSE_FILE")
readonly COMPOSE_FILE
# Where the services see the agnello-pki volume (certificates and secrets), and the demo customer.
readonly PKI_DIR=/run/agnello
readonly DEMO_USER=cliente
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

# Runs curl from a throwaway netlab container on the private network that uses dnsmasq as its
# resolver and trusts only the environment's CA. The CA certificate is streamed in from the Order
# Service's read-only copy of the agnello-pki volume, so no host path is mounted.
# Prints the body, then the HTTP status on a last line of its own ("000" when no response came).
curl_from_network() {
  docker exec "$CONTAINER_ORDER" cat "$PKI_DIR/ca.crt" 2>/dev/null |
    docker run -i --rm --network "$NETWORK" --dns "$DNS_SERVER" --entrypoint sh "$NETLAB_IMAGE" -c \
      'cat > /tmp/ca.crt && curl -s --max-time 10 --cacert /tmp/ca.crt -w "\n%{http_code}" "$@"' curl "$@" 2>&1
}

# Last line of curl_from_network's output: the HTTP status.
status_of() {
  printf '%s\n' "$1" | tail -n 1
}

# Everything but the last line: the body.
body_of() {
  printf '%s\n' "$1" | sed '$d'
}

# Beginning of a response, for failure details (a wine list is long).
response_excerpt() {
  printf '%s' "$1" | tr '\n' ' ' | cut -c1-200
}

# The demo customer's password, generated into the agnello-pki volume by the pki job.
demo_password() {
  docker exec "$CONTAINER_ORDER" cat "$PKI_DIR/demo-password" 2>/dev/null
}

check_netlab_image() {
  local description="tools: the $NETLAB_IMAGE image (curl, tcpdump, tshark) is available"
  if docker image inspect "$NETLAB_IMAGE" >/dev/null 2>&1 ||
    docker compose -f "$COMPOSE_FILE" --profile tools build netlab >/dev/null 2>&1; then
    pass "$description"
    netlab_ready=1
  else
    fail "$description" "could not build it: docker compose -f infra/docker-compose.yml --profile tools build netlab"
    netlab_ready=0
  fi
}

require_netlab() {
  [ "$netlab_ready" = 1 ] && return 0
  fail "$1" "not run: the $NETLAB_IMAGE image is not available"
  return 1
}

# Only HTTPS: a plain-HTTP request reaches no application code, Tomcat answers 400 before it.
check_catalog_refuses_plain_http() {
  local url="http://catalog.$ZONE:$CATALOG_PORT/health"
  local description="tls: the Catalog Service refuses plain HTTP on catalog.$ZONE:$CATALOG_PORT"
  local response
  require_healthy "$description" "$catalog_state" "Catalog Service" || return
  require_netlab "$description" || return
  response=$(curl_from_network "$url")
  if [ "$(status_of "$response")" = 400 ] && printf '%s\n' "$response" | grep -qF 'requires TLS'; then
    pass "$description"
  else
    fail "$description" "GET $url answered $(status_of "$response"): $(response_excerpt "$(body_of "$response")")"
  fi
}

# curl checks the certificate against the internal CA and the name catalog.agnello.internal.
check_catalog_https_certificate() {
  local url="https://catalog.$ZONE:$CATALOG_PORT/health"
  local description="tls: catalog.$ZONE:$CATALOG_PORT answers HTTPS with a certificate the internal CA issued for that name"
  local response
  require_healthy "$description" "$dnsmasq_state" dnsmasq || return
  require_healthy "$description" "$catalog_state" "Catalog Service" || return
  require_netlab "$description" || return
  response=$(curl_from_network "$url")
  if [ "$(status_of "$response")" = 200 ]; then
    pass "$description"
  else
    fail "$description" "GET $url failed: $(response_excerpt "$response")"
  fi
}

check_catalog_requires_token() {
  local url="https://catalog.$ZONE:$CATALOG_PORT/v1/vinhos"
  local description="jwt: the Catalog Service answers 401 to a wine list read without a token"
  local response
  require_healthy "$description" "$catalog_state" "Catalog Service" || return
  require_netlab "$description" || return
  response=$(curl_from_network "$url")
  if [ "$(status_of "$response")" = 401 ]; then
    pass "$description"
  else
    fail "$description" "GET $url answered $(status_of "$response"): $(response_excerpt "$(body_of "$response")")"
  fi
}

check_login_refuses_wrong_password() {
  local url="https://order.$ZONE:$ORDER_PORT/v1/auth/token"
  local description="auth: the Order Service answers 401 to a login with a wrong password"
  local response
  require_healthy "$description" "$order_state" "Order Service" || return
  require_netlab "$description" || return
  response=$(curl_from_network -X POST -u "$DEMO_USER:not-the-password" "$url")
  if [ "$(status_of "$response")" = 401 ]; then
    pass "$description"
  else
    fail "$description" "POST $url answered $(status_of "$response")"
  fi
}

# Logs in as the demo customer; sets customer_token on success.
check_login_issues_token() {
  local url="https://order.$ZONE:$ORDER_PORT/v1/auth/token"
  local description="auth: the Order Service trades the demo customer's password for a JWT (HTTP Basic, POST /v1/auth/token)"
  local response password
  customer_token=
  require_healthy "$description" "$order_state" "Order Service" || return
  require_netlab "$description" || return
  if ! password=$(demo_password) || [ -z "$password" ]; then
    fail "$description" "cannot read $PKI_DIR/demo-password in $CONTAINER_ORDER"
    return
  fi
  response=$(curl_from_network -X POST -u "$DEMO_USER:$password" "$url")
  customer_token=$(body_of "$response" | sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p')
  if [ "$(status_of "$response")" = 200 ] && [ -n "$customer_token" ]; then
    pass "$description"
  else
    fail "$description" "POST $url answered $(status_of "$response"): $(response_excerpt "$(body_of "$response")")"
  fi
}

check_order_requires_token() {
  local url="https://order.$ZONE:$ORDER_PORT/v1/carrinhos/1"
  local description="jwt: the Order Service answers 401 to a Carrinho read without a token"
  local response
  require_healthy "$description" "$order_state" "Order Service" || return
  require_netlab "$description" || return
  response=$(curl_from_network "$url")
  if [ "$(status_of "$response")" = 401 ]; then
    pass "$description"
  else
    fail "$description" "GET $url answered $(status_of "$response")"
  fi
}

# The customer's token names the Order Service as its audience: the Catalog Service must refuse it.
check_catalog_refuses_customer_token() {
  local url="https://catalog.$ZONE:$CATALOG_PORT/v1/vinhos"
  local description="jwt: the Catalog Service answers 401 to the customer's token (issued for the Order Service)"
  local response
  require_healthy "$description" "$catalog_state" "Catalog Service" || return
  require_netlab "$description" || return
  if [ -z "$customer_token" ]; then
    fail "$description" "not run: no customer token (the login check failed)"
    return
  fi
  response=$(curl_from_network -H "Authorization: Bearer $customer_token" "$url")
  if [ "$(status_of "$response")" = 401 ]; then
    pass "$description"
  else
    fail "$description" "GET $url answered $(status_of "$response")"
  fi
}

# Succeeds when a response body is the seeded Carrinho 1 with its items and total.
is_seeded_cart() {
  printf '%s\n' "$1" | grep -qF '"idCarrinho":1' &&
    printf '%s\n' "$1" | grep -qF "\"nomeVinho\":\"$SEED_WINE\"" &&
    printf '%s\n' "$1" | grep -qF "\"nomeVinho\":\"$SEED_CART_WINE_2\"" &&
    printf '%s\n' "$1" | grep -qF "\"total\":$SEED_CART_TOTAL"
}

# The Order Service stores only wine ids and quantities: the names and the total can only come
# from a call to the Catalog Service, by DNS name, over HTTPS, with a service token the Catalog
# Service accepted.
check_order_cart_by_dns_name() {
  local url="https://order.$ZONE:$ORDER_PORT/v1/carrinhos/1"
  local description="order: with the customer's token, Carrinho 1 comes back with the Catalog Service's names and prices and total $SEED_CART_TOTAL (Order to Catalog over HTTPS with a service JWT)"
  local response
  require_healthy "$description" "$dnsmasq_state" dnsmasq || return
  require_healthy "$description" "$order_state" "Order Service" || return
  require_netlab "$description" || return
  if [ -z "$customer_token" ]; then
    fail "$description" "not run: no customer token (the login check failed)"
    return
  fi
  response=$(curl_from_network -H "Authorization: Bearer $customer_token" "$url")
  if [ "$(status_of "$response")" = 200 ] && is_seeded_cart "$(body_of "$response")"; then
    pass "$description"
  else
    fail "$description" "GET $url answered $(status_of "$response"): $(response_excerpt "$(body_of "$response")")"
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
check_netlab_image
check_catalog_refuses_plain_http
check_catalog_https_certificate
check_catalog_requires_token
check_login_refuses_wrong_password
check_login_issues_token
check_order_requires_token
check_catalog_refuses_customer_token
check_order_cart_by_dns_name
check_order_resolved_catalog_through_dnsmasq

printf '\nSummary: %d PASS, %d FAIL\n' "$passed" "$failed"
[ "$failed" -eq 0 ]

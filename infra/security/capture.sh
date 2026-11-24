#!/usr/bin/env bash
# Wireshark demonstration: the same requests, captured twice on the Order Service's network
# interface, first in plain HTTP (docker-compose.insecure.yml), then in HTTPS (the default).
#
#   1. login (HTTP Basic) and a Carrinho read (JWT), from a client container on agnello-net;
#   2. the Order Service's own calls to the Catalog Service (service JWT);
#   3. in plain HTTP, an "attacker" replays the service token it read in the capture.
#
# Writes infra/security/captures/inseguro.pcap and seguro.pcap, to open in Wireshark, and prints
# what tshark (command-line Wireshark) reads in each one. Leaves the environment in HTTPS mode.
#
# Run from the repository root, with the environment up:  infra/security/capture.sh

# No pipefail: tshark is often cut short by head on purpose.
set -eu
# Git Bash on Windows would rewrite container paths such as /run/agnello into Windows paths.
export MSYS_NO_PATHCONV=1

HERE=$(cd "$(dirname "$0")" && pwd)
# ...so host paths handed to docker are converted here instead (C:/... on Git Bash).
command -v cygpath >/dev/null 2>&1 && HERE=$(cygpath -m "$HERE")
readonly HERE
readonly INFRA=$(dirname "$HERE")
readonly OUT="$HERE/captures"
readonly COMPOSE=(docker compose -f "$INFRA/docker-compose.yml")
readonly COMPOSE_INSECURE=(docker compose -f "$INFRA/docker-compose.yml" -f "$INFRA/docker-compose.insecure.yml")
readonly NETWORK=agnello-net
readonly DNS_SERVER=172.28.0.2
readonly NETLAB=agnello-netlab
readonly SNIFFER=agnello-sniffer
readonly ORDER=agnello-order
readonly PKI_DIR=/run/agnello
# tshark only decodes HTTP and TLS on their usual ports unless told: these are the services' ports.
readonly DECODE_HTTP=(-d tcp.port==8081,http -d tcp.port==8082,http)
readonly DECODE_TLS=(-d tcp.port==8081,tls -d tcp.port==8082,tls)

title() {
  printf '\n\033[1m== %s\033[0m\n' "$1"
}

# curl from a throwaway container on agnello-net that resolves through dnsmasq and trusts the internal CA.
client() {
  docker exec "$ORDER" cat "$PKI_DIR/ca.crt" |
    docker run -i --rm --network "$NETWORK" --dns "$DNS_SERVER" --entrypoint sh "$NETLAB" -c \
      'cat > /tmp/ca.crt && curl -s --max-time 10 --cacert /tmp/ca.crt "$@"' curl "$@"
}

# tshark on a capture file, streamed in, so no host path is mounted.
tshark_on() {
  local pcap=$1
  shift
  docker run -i --rm --network none --entrypoint tshark "$NETLAB" -r - "$@" < "$pcap" 2>/dev/null
}

# Starts tcpdump inside the Order Service's network namespace: it sees everything the Order
# Service sends and receives, the client's requests and its own calls to the Catalog Service.
start_sniffer() {
  docker rm -f "$SNIFFER" >/dev/null 2>&1 || true
  docker run -d --name "$SNIFFER" --network "container:$ORDER" --cap-add NET_RAW --cap-add NET_ADMIN \
    --entrypoint tcpdump "$NETLAB" -i eth0 -U -s 0 -w /tmp/capture.pcap 'tcp port 8081 or tcp port 8082' >/dev/null
  for _ in $(seq 1 20); do
    docker logs "$SNIFFER" 2>&1 | grep -q 'listening on' && return 0
    sleep 0.5
  done
  echo "tcpdump did not start: $(docker logs "$SNIFFER" 2>&1 | tail -n 1)" >&2
  return 1
}

stop_sniffer() {
  local dest=$1
  sleep 1
  docker kill --signal INT "$SNIFFER" >/dev/null
  docker wait "$SNIFFER" >/dev/null
  docker cp "$SNIFFER:/tmp/capture.pcap" "$dest" >/dev/null
  docker rm "$SNIFFER" >/dev/null
}

# The customer's journey: log in with the password, then read Carrinho 1 with the token.
customer_journey() {
  local scheme=$1 password token
  password=$(docker exec "$ORDER" cat "$PKI_DIR/demo-password")
  token=$(client -X POST -u "cliente:$password" "$scheme://order.agnello.internal:8082/v1/auth/token" |
    sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p')
  [ -n "$token" ] || { echo "login failed in $scheme mode" >&2; return 1; }
  client -o /dev/null -w "  login: 200, Carrinho 1: %{http_code}\n" \
    -H "Authorization: Bearer $token" "$scheme://order.agnello.internal:8082/v1/carrinhos/1"
}

# The payload (claims) of a JWT: base64url, unpadded.
jwt_claims() {
  local payload
  payload=$(printf '%s' "$1" | cut -d. -f2 | tr '_-' '/+')
  while [ $(( ${#payload} % 4 )) -ne 0 ]; do payload="$payload="; done
  printf '%s' "$payload" | base64 -d 2>/dev/null
}

mkdir -p "$OUT"
"${COMPOSE[@]}" --profile tools build -q netlab

# ---------------------------------------------------------------------------------------------
title "1/2  SEM SEGURANÇA: HTTP puro (docker-compose.insecure.yml)"
"${COMPOSE_INSECURE[@]}" up -d >/dev/null 2>&1
start_sniffer
customer_journey http
stop_sniffer "$OUT/inseguro.pcap"
echo "  captura: $OUT/inseguro.pcap"

title "O que o Wireshark lê em inseguro.pcap (filtro: http.request)"
tshark_on "$OUT/inseguro.pcap" "${DECODE_HTTP[@]}" -Y http.request -T fields -E separator=' ' \
  -e ip.src -e ip.dst -e http.request.method -e http.request.uri | sed 's/^/  /'

title "Senha do cliente, em claro (campo http.authbasic)"
tshark_on "$OUT/inseguro.pcap" "${DECODE_HTTP[@]}" -Y http.authbasic -T fields -e http.authbasic | sed 's/^/  /'

title "Tokens JWT, em claro (campo http.authorization)"
tshark_on "$OUT/inseguro.pcap" "${DECODE_HTTP[@]}" -Y 'http.authorization contains "Bearer"' -T fields \
  -e ip.dst -e http.authorization | cut -c1-110 | sed 's/^/  /; s/$/.../'

title "Dados de negócio, em claro (resposta do Catalog Service: nome e preço)"
body_hex=$(tshark_on "$OUT/inseguro.pcap" "${DECODE_HTTP[@]}" -Y 'http.response and tcp.srcport==8081' -T fields \
  -e http.file_data | head -n 1)
# tshark prints the body as hex: turn it back into text.
printf '  %b\n' "$(printf '%s' "$body_hex" | tr -d ':' | sed 's/../\\x&/g')" | cut -c1-160

title "Ataque: reutilizar o token de serviço lido na captura (replay)"
stolen=$(tshark_on "$OUT/inseguro.pcap" "${DECODE_HTTP[@]}" -Y 'http.request and tcp.dstport==8081' \
  -T fields -e http.authorization | head -n 1 | sed 's/^Bearer //')
echo "  claims do token roubado: $(jwt_claims "$stolen")"
client -o /dev/null -w "  atacante -> GET http://catalog.agnello.internal:8081/v1/vinhos com o token roubado: HTTP %{http_code}\n" \
  -H "Authorization: Bearer $stolen" http://catalog.agnello.internal:8081/v1/vinhos
echo "  O JWT autentica, mas não esconde nada: quem lê o tráfego se passa pelo Order Service até o token expirar (60 s)."

# ---------------------------------------------------------------------------------------------
title "2/2  COM SEGURANÇA: HTTPS (TLS 1.3) com o mesmo JWT (docker-compose.yml)"
"${COMPOSE[@]}" up -d >/dev/null 2>&1
start_sniffer
customer_journey https
stop_sniffer "$OUT/seguro.pcap"
echo "  captura: $OUT/seguro.pcap"

title "O que o Wireshark lê em seguro.pcap"
printf '  requisições HTTP legíveis: %s\n' "$(tshark_on "$OUT/seguro.pcap" "${DECODE_TLS[@]}" -Y http.request | wc -l | tr -d ' ')"
printf '  ocorrências de "Bearer", "eyJ" (início de um JWT) ou "Cabernet" nos bytes: %s\n' \
  "$(grep -a -c -E 'Bearer|eyJ|Cabernet' "$OUT/seguro.pcap" || true)"
echo "  handshakes TLS (filtro: tls.handshake), servidor pedido (SNI) e versão negociada:"
tshark_on "$OUT/seguro.pcap" "${DECODE_TLS[@]}" -Y 'tls.handshake.type == 2' -T fields -E separator=' ' \
  -e ip.src -e ip.dst -e tls.handshake.extensions.supported_version | sort -u |
  sed 's/0x0304/TLS 1.3/; s/^/    ServerHello /'
tshark_on "$OUT/seguro.pcap" "${DECODE_TLS[@]}" -Y 'tls.handshake.type == 1' -T fields -E separator=' ' \
  -e ip.src -e ip.dst -e tls.handshake.extensions_server_name | sort -u | sed 's/^/    ClientHello /'
printf '  pacotes com registros "Application Data" (conteúdo cifrado): %s\n' \
  "$(tshark_on "$OUT/seguro.pcap" "${DECODE_TLS[@]}" -Y 'tls.record.opaque_type == 23 || tls.record.content_type == 23' | wc -l | tr -d ' ')"

title "Ataque de novo: o mesmo pedido em HTTP puro contra o Catalog Service em HTTPS"
client -w " (HTTP %{http_code})\n" -H "Authorization: Bearer $stolen" http://catalog.agnello.internal:8081/v1/vinhos |
  tr '\n' ' ' | sed 's/^/  /'
echo
echo
echo "Abra as duas capturas no Wireshark. Nas portas 8081 e 8082 use Analyze > Decode As... > TCP port 8081/8082:"
echo "HTTP para inseguro.pcap e TLS para seguro.pcap. Filtros úteis: http.request, http.authorization, tls."

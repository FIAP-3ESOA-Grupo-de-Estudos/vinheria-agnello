#!/bin/sh
# One-shot job: generates, once, the environment's TLS certificates and secrets into the
# agnello-pki volume, mounted at /pki. Nothing here is ever written to the repository.
#
#   ca.crt                       the internal CA (its private key is thrown away after signing)
#   catalog.crt, catalog.key     the Catalog Service's certificate and key
#   order.crt, order.key         the Order Service's certificate and key
#   jwt.secret                   the HS256 key, base64, 384 bits, shared by the two services
#   demo-password                the password of the demo customer "cliente"
#
# Runs again as a no-op. To regenerate everything: docker compose -f infra/docker-compose.yml down -v

set -eu

readonly PKI=/pki
# The services run as uid 10001 (catalog) and 10002 (order), both in group 10000 (agnello).
readonly SERVICE_GROUP=10000
readonly CA_DAYS=825
readonly LEAF_DAYS=397

if [ -f "$PKI/.complete" ]; then
  echo "pki: already generated, nothing to do"
  exit 0
fi

umask 077
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

# P-256 keys: small and fast, and every TLS client in the environment speaks ECDHE-ECDSA.
openssl req -x509 -new -newkey ec -pkeyopt ec_paramgen_curve:P-256 -nodes \
  -keyout "$work/ca.key" -out "$work/ca.crt" -days "$CA_DAYS" \
  -subj "/O=Vinheria Agnello/CN=Vinheria Agnello Internal CA" \
  -addext "basicConstraints=critical,CA:TRUE,pathlen:0" \
  -addext "keyUsage=critical,keyCertSign,cRLSign" 2>/dev/null

# A server certificate for one service: its DNS name in the zone, plus localhost and 127.0.0.1
# for the published port and the container's own healthcheck.
issue() {
  name=$1
  openssl req -new -newkey ec -pkeyopt ec_paramgen_curve:P-256 -nodes \
    -keyout "$work/$name.key" -out "$work/$name.csr" \
    -subj "/O=Vinheria Agnello/CN=$name.agnello.internal" 2>/dev/null
  cat > "$work/$name.ext" <<EOF
basicConstraints=critical,CA:FALSE
keyUsage=critical,digitalSignature
extendedKeyUsage=serverAuth
subjectAltName=DNS:$name.agnello.internal,DNS:localhost,IP:127.0.0.1
EOF
  openssl x509 -req -in "$work/$name.csr" -CA "$work/ca.crt" -CAkey "$work/ca.key" \
    -CAcreateserial -days "$LEAF_DAYS" -sha256 -extfile "$work/$name.ext" \
    -out "$work/$name.crt" 2>/dev/null
  echo "pki: issued $name.agnello.internal"
}

issue catalog
issue order

openssl rand -base64 48 > "$work/jwt.secret"
openssl rand -base64 18 | tr -d '/+=' > "$work/demo-password"

# Certificates are public; keys and secrets are readable by the two services' group only.
for file in ca.crt catalog.crt order.crt; do
  install -m 0644 -o root -g "$SERVICE_GROUP" "$work/$file" "$PKI/$file"
done
for file in catalog.key order.key jwt.secret demo-password; do
  install -m 0640 -o root -g "$SERVICE_GROUP" "$work/$file" "$PKI/$file"
done
chown root:"$SERVICE_GROUP" "$PKI"
chmod 0750 "$PKI"
# ca.key is not copied: with the CA key gone, nobody can mint another certificate this environment trusts.
touch "$PKI/.complete"
echo "pki: generated CA, certificates, JWT key and demo password in the agnello-pki volume"

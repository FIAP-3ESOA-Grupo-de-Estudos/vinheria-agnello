FROM alpine:3.24
RUN apk add --no-cache openssl
COPY init-pki.sh /init-pki.sh
ENTRYPOINT ["sh", "/init-pki.sh"]

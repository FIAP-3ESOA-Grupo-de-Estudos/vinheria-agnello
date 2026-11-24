# Vinheria Agnello: infraestrutura e serviços essenciais (fase 07, item 1)

Ambiente Docker Compose que simula parte da arquitetura da Vinheria Agnello: dois microsserviços, o Catalog Service e o Order Service, numa rede privada com DNS e DHCP. Os dois são registrados na zona `agnello.internal` e conversam por nome. Este é o item 1 da fase 07 ("Infraestrutura e Serviços Essenciais"); o item 2 (pipeline Jenkins) e o item 3 (comunicação segura e DevSecOps) partem desta base. **O item 3 já está aplicado aqui:** os dois serviços falam só HTTPS e exigem JWT; veja [`security/README.md`](security/README.md).

## 1. Arquitetura em palavras

Tudo vive numa rede bridge do Docker, `agnello-net` (172.28.0.0/24). Os contêineres se chamam `agnello-dnsmasq`, `agnello-catalog`, `agnello-order` e `agnello-probe`. Endereços, nomes, MACs e portas estão em [`NETWORK.md`](NETWORK.md).

- **dnsmasq** (Serviço de infraestrutura): faz DNS e DHCP. É autoritativo pela zona `agnello.internal`, responde NXDOMAIN para nomes desconhecidos dentro dela e encaminha o resto para 8.8.8.8 e 1.1.1.1. Os registros `catalog` e `order` são o registro dos microsserviços no DNS.
- **Catalog Service**: lista os vinhos ativos e devolve um vinho por id (`GET /v1/vinhos`, `GET /v1/vinhos/{id}`).
- **Order Service**: devolve um Carrinho (`GET /v1/carrinhos/{id}`). Ele guarda só o id do vinho e a quantidade; o nome e o preço vêm do Catalog Service, pelo nome DNS (`CATALOG_URL`), a cada leitura.
- **probe**: job de uso único que faz um handshake DHCP de verdade com o dnsmasq e termina.
- **pki** (item 3): job de uso único que gera, na primeira subida, os certificados TLS, a chave JWT e a senha de demonstração no volume `agnello-pki`.

Cada microsserviço guarda os próprios dados, em memória (Database per Service do documento de arquitetura). Java 17, Spring Boot 3.5 e Maven; a compilação acontece dentro do Docker.

```
                   rede agnello-net (172.28.0.0/24)

 localhost:8082 ─▶ ┌────────────────────┐                               ┌────────────────────┐ ◀─ localhost:8081
                   │ Order Service      │ HTTPS + JWT, por nome         │ Catalog Service    │
                   │ 172.28.0.12  :8082 │ ────────────────────────────▶ │ 172.28.0.11  :8081 │
                   └────────────────────┘ catalog.agnello.internal:8081 └────────────────────┘

 dnsmasq 172.28.0.2 (DNS e DHCP): é o resolvedor do Order Service.
 probe: pede um endereço ao DHCP, recebe um do pool e sai.
```

A ordem de início é guiada pela saúde: dnsmasq, depois os dois microsserviços, e por último o probe. O Order Service não depende do Catalog Service para subir. Se o Catalog Service estiver fora do ar, o Order Service continua saudável e responde 503 ao ler um Carrinho.

## 2. Pré-requisitos

- Docker com Compose v2 ou superior.
- `curl` e bash na máquina host (para o `verify.sh` e os exemplos).
- Portas 8081 e 8082 livres no host e a sub-rede 172.28.0.0/24 livre.
- Java e Maven locais não são necessários para rodar: os microsserviços compilam dentro do Docker.

## 3. Comandos

Rode tudo a partir da raiz do repositório. Não há credenciais no repositório nem arquivo `.env`: o job `pki` gera certificados e segredos no volume `agnello-pki`.

```bash
docker compose -f infra/docker-compose.yml up -d --build   # inicia tudo
infra/verify.sh                                            # verifica: PASS ou FAIL por check, código 0 se tudo passa
docker compose -f infra/docker-compose.yml down           # para e remove contêineres e rede (o volume agnello-pki fica)
docker compose -f infra/docker-compose.yml down -v        # idem, e apaga certificados e segredos
```

O `up` retorna quando o dnsmasq e os dois microsserviços estão saudáveis e o probe já foi iniciado. O `verify.sh` leva poucos segundos e pode rodar quantas vezes quiser, sem reset.

## 4. Saída esperada do `verify.sh`

```
PASS  startup: dnsmasq is healthy
PASS  startup: Catalog Service is healthy
PASS  startup: Order Service is healthy
PASS  dns: catalog.agnello.internal resolves to 172.28.0.11
PASS  dns: order.agnello.internal resolves to 172.28.0.12
PASS  dhcp: probe completes discover, offer, request and acknowledge, gets a pool address and registers in DNS
PASS  tools: the agnello-netlab image (curl, tcpdump, tshark) is available
PASS  tls: the Catalog Service refuses plain HTTP on catalog.agnello.internal:8081
PASS  tls: catalog.agnello.internal:8081 answers HTTPS with a certificate the internal CA issued for that name
PASS  jwt: the Catalog Service answers 401 to a wine list read without a token
PASS  auth: the Order Service answers 401 to a login with a wrong password
PASS  auth: the Order Service trades the demo customer's password for a JWT (HTTP Basic, POST /v1/auth/token)
PASS  jwt: the Order Service answers 401 to a Carrinho read without a token
PASS  jwt: the Catalog Service answers 401 to the customer's token (issued for the Order Service)
PASS  order: with the customer's token, Carrinho 1 comes back with the Catalog Service's names and prices and total 259.70 (Order to Catalog over HTTPS with a service JWT)
PASS  dns: dnsmasq's query log shows catalog.agnello.internal looked up from the Order Service (172.28.0.12)

Summary: 16 PASS, 0 FAIL
```

Um check que falha mostra `FAIL` e, na linha de baixo, o motivo. Com a pilha parada, os checks de início dizem qual comando rodar.

## 5. Como capturar as evidências

Cada bloco é um print para o trabalho. O MAC, o número da transação DHCP e o endereço do pool variam de uma máquina para outra.

**DNS.** Um contêiner descartável pergunta **diretamente ao dnsmasq** (`172.28.0.2`); a linha `Server: 172.28.0.2` mostra quem respondeu. O resolvedor interno do Docker fica na frente do Order Service e encaminha ao dnsmasq as consultas da zona, então a prova de que o dnsmasq trabalha é perguntar a ele direto e ler o log dele.

```bash
docker run --rm --network agnello-net --entrypoint nslookup agnello-dnsmasq catalog.agnello.internal 172.28.0.2
```

```
Server:		172.28.0.2
Address:	172.28.0.2:53

Name:	catalog.agnello.internal
Address: 172.28.0.11
```

Troque `catalog` por `order` (172.28.0.12). Um nome desconhecido na zona responde `NXDOMAIN`. O log mostra que o próprio Order Service usa o dnsmasq (rode antes o `verify.sh` ou a leitura do Carrinho mais abaixo):

```bash
docker logs agnello-dnsmasq 2>&1 | grep -E 'query\[A\] catalog\.agnello\.internal from 172\.28\.0\.12' | sort -u
```

```
dnsmasq[1]: query[A] catalog.agnello.internal from 172.28.0.12
```

**DHCP e lease.** O probe imprime discover, select (o request) e a confirmação, mas **nunca o offer**: o `udhcpc` do busybox não o escreve. Por isso o print tem duas partes, a saída do probe e o log do dnsmasq, que mostra os quatro passos.

```bash
docker start -a agnello-probe
docker logs agnello-dnsmasq 2>&1 | grep -E 'DHCP(DISCOVER|OFFER|REQUEST|ACK)' | tail -n 4
docker exec agnello-dnsmasq cat /var/lib/misc/dnsmasq.leases
docker run --rm --network agnello-net --entrypoint nslookup agnello-dnsmasq probe.agnello.internal 172.28.0.2
```

```
probe: MAC address 02:42:ac:1c:00:03
udhcpc: started, v1.37.0
udhcpc: broadcasting discover
udhcpc: broadcasting select for 172.28.0.199, server 172.28.0.2
udhcpc: lease of 172.28.0.199 obtained from 172.28.0.2, lease time 43200

dnsmasq-dhcp[1]: 1793299294 DHCPDISCOVER(eth0) 02:42:ac:1c:00:03
dnsmasq-dhcp[1]: 1793299294 DHCPOFFER(eth0) 172.28.0.199 02:42:ac:1c:00:03
dnsmasq-dhcp[1]: 1793299294 DHCPREQUEST(eth0) 172.28.0.199 02:42:ac:1c:00:03
dnsmasq-dhcp[1]: 1793299294 DHCPACK(eth0) 172.28.0.199 02:42:ac:1c:00:03 probe

1795478273 02:42:ac:1c:00:03 172.28.0.199 probe 01:02:42:ac:1c:00:03

Name:	probe.agnello.internal
Address: 172.28.0.199
```

O lease tem cinco colunas: instante de expiração, MAC, endereço, nome e identificador do cliente. O endereço está dentro do pool (100 a 199) e o nome `probe` passou a resolver na zona: DHCP e DNS cooperando.

**Order Service chamando o Catalog Service.** O Order Service não guarda nome nem preço: pergunta ao Catalog Service, pelo nome DNS, por HTTPS e com um token de serviço. Da máquina host, primeiro faça login (no Windows, acrescente `--ssl-no-revoke` a cada `curl`; detalhes em [`security/README.md`](security/README.md)):

```bash
docker exec agnello-order cat /run/agnello/ca.crt > infra/security/ca.crt
SENHA=$(docker exec agnello-order cat /run/agnello/demo-password)
TOKEN=$(curl -s --cacert infra/security/ca.crt -u "cliente:$SENHA" -X POST https://localhost:8082/v1/auth/token \
  | sed 's/.*"access_token":"\([^"]*\)".*/\1/')
curl -s --cacert infra/security/ca.crt -H "Authorization: Bearer $TOKEN" https://localhost:8082/v1/carrinhos/1
```

```
{"idCarrinho":1,"itens":[{"idVinho":1,"nomeVinho":"Cabernet Sauvignon Reserva","quantidade":2,"precoUnitario":89.90},{"idVinho":4,"nomeVinho":"Chardonnay Premium","quantidade":1,"precoUnitario":79.90}],"total":259.70}
```

Com o Catalog Service parado, o Order Service continua saudável e responde 503, em cerca de 2 segundos (o timeout de conexão), porque o endereço de um contêiner parado não recusa a conexão:

```bash
docker stop agnello-catalog
curl -s --cacert infra/security/ca.crt -o /dev/null -w 'HTTP %{http_code}\n' -H "Authorization: Bearer $TOKEN" https://localhost:8082/v1/carrinhos/1   # HTTP 503
curl -s --cacert infra/security/ca.crt https://localhost:8082/health   # {"status":"UP"}
docker start agnello-catalog
```

## 6. DHCP simulado

**O DHCP deste ambiente é simulado.** O Catalog Service e o Order Service **não** recebem endereço por DHCP: o Docker lhes dá endereços e MACs fixos (`ipv4_address` e `mac_address` no `docker-compose.yml`). A tabela de reservas do dnsmasq (`dhcp-host=MAC,endereço,nome`) lista os dois e apenas **espelha** esses valores e os registros DNS (`host-record`), como a reserva de endereços do DHCP do Windows Server.

**O que é real:** o probe faz um handshake DHCP completo (discover, offer, request, acknowledge) contra o dnsmasq, recebe um endereço do pool, o dnsmasq grava o lease com o nome `probe`, e o nome passa a resolver como `probe.agnello.internal`. O probe roda em modo só-handshake (`udhcpc -s /bin/true`): o script padrão trocaria o endereço que o Docker deu ao próprio contêiner pelo do lease.

**E se o handshake real não funcionasse?** O `verify.sh` não tem modo de fallback: o check de DHCP dá `FAIL` com o motivo. Comparar a tabela de reservas com os registros DNS provaria só consistência de configuração, não um servidor DHCP funcionando, e teria de ser apresentado como "DHCP simulado, sem handshake".

## 7. Por que `agnello.internal`, e por que só o Order Service usa a zona

**Por que não `.local`:** o sufixo `.local` é reservado ao multicast DNS (mDNS), que colide com o DNS comum. `.internal` é um sufixo reservado para uso privado. Não troque a zona por `.local`.

**Por que só o Order Service usa a zona:** só ele precisa resolver um nome, o `catalog.agnello.internal`, e por isso tem o dnsmasq como resolvedor (`dns: 172.28.0.2` no Compose). A máquina host não usa o dnsmasq e a rede bridge pode viver dentro de uma VM do Docker, então não conte com resolver `catalog.agnello.internal` no host. O host usa as portas publicadas, `localhost:8081` e `localhost:8082`, só no loopback (`127.0.0.1`): mesmo com TLS e JWT, outras máquinas da rede não alcançam os microsserviços (defesa em profundidade). Os certificados valem também para `localhost` e `127.0.0.1`.

**Cuidado com o dnsmasq:** não use `docker stop` nele. O Docker libera o 172.28.0.2 e o próximo contêiner de endereço dinâmico (o probe) pode ficar com ele, e o dnsmasq não volta ("Address already in use"). Para pausar, use `docker pause agnello-dnsmasq` e `docker unpause agnello-dnsmasq`.

## 8. Testes unitários

Cada microsserviço tem testes de API (MockMvc) que não precisam de Docker. No Order Service, o Catalog Service é simulado na fronteira HTTP (cobre o 503 e confere o token de serviço de cada chamada). Os testes `*SecurityTest` cobrem as recusas de JWT e de login. Com Java 17 e Maven:

```bash
(cd services/catalog-service && mvn -B test)
(cd services/order-service && mvn -B test)
```

## 9. Fora do escopo

API Gateway, os outros seis microsserviços da arquitetura entregue (Authentication, User, Inventory, Payment, Membership e Quality Monitoring), escrita em Carrinhos e o fluxo de Pedido, banco de dados, retry e circuit breaker, e vários dnsmasq ou sub-redes. Autenticação, TLS e a captura com Wireshark são o item 3, em [`security/README.md`](security/README.md); o pipeline Jenkins é o item 2.

Para comparar com o tráfego sem criptografia, [`docker-compose.insecure.yml`](docker-compose.insecure.yml) sobe os mesmos serviços em HTTP puro; o `security/capture.sh` usa esse modo e volta ao HTTPS no fim. Para anexar um novo contêiner a `agnello-net`, dê a ele um endereço fixo fora do pool (100 a 199) e registre-o no `dnsmasq.conf`, no `docker-compose.yml`, no `NETWORK.md` e no `verify.sh`.

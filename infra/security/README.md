# Vinheria Agnello: comunicação segura e DevSecOps (fase 07, item 3)

Este item parte do ambiente do item 1 ([`../README.md`](../README.md)): o Catalog Service e o Order Service na rede `agnello-net`, com DNS e DHCP do dnsmasq. Aqui os dois passam a se autenticar com **JWT** e a conversar só por **HTTPS (TLS 1.3)**. Uma captura de pacotes mostra, lado a lado, o que um atacante na rede vê com e sem criptografia.

| Pedido do enunciado | Onde está |
|---|---|
| Definir como os microsserviços trocam informações | Seção 1: REST sobre HTTPS, autenticado por JWT |
| Implementar autenticação (básica, token etc.) | Seção 2: HTTP Basic para o login, JWT nas chamadas, token de serviço entre os dois |
| Mostrar o tráfego sem segurança e a importância da criptografia | Seção 4: `capture.sh` gera `inseguro.pcap` e `seguro.pcap` para o Wireshark |
| DevSecOps | Seção 5: segredos fora do Git, testes de segurança automatizados, proposta de estágios no Jenkins |

## 1. Como os microsserviços trocam informações

**REST síncrono sobre HTTPS, com um JWT em cada requisição.** O Order Service precisa do nome e do preço de um vinho no momento em que monta o Carrinho; a resposta é imediata e pequena, então uma chamada REST resolve. Mensageria (RabbitMQ, Kafka) faria sentido para eventos que não pedem resposta na hora, como "pedido pago" avisando o estoque. Ela não entra aqui porque nenhum dos dois serviços tem um evento desses.

```
 cliente (curl)                     Order Service                           Catalog Service
 172.28.0.x                         order.agnello.internal:8082             catalog.agnello.internal:8081
      │                                   │                                        │
      │ 1. POST /v1/auth/token            │                                        │
      │    Authorization: Basic …  ─────▶ │ confere a senha (BCrypt)               │
      │ ◀──── {"access_token": JWT A}     │                                        │
      │                                   │                                        │
      │ 2. GET /v1/carrinhos/1            │                                        │
      │    Authorization: Bearer JWT A ─▶ │ valida JWT A                           │
      │                                   │ 3. GET /v1/vinhos/1                    │
      │                                   │    Authorization: Bearer JWT B ──────▶ │ valida JWT B
      │                                   │ ◀────────── {"nome": …, "preco": …}    │
      │ ◀──── Carrinho com nomes e total  │                                        │
      │                                   │                                        │
      └──────────── TLS 1.3 ──────────────┴──────────────── TLS 1.3 ───────────────┘
```

Os dois tokens são diferentes de propósito:

| Claim | JWT A (do cliente) | JWT B (de serviço) |
|---|---|---|
| `iss` (quem emitiu) | `order.agnello.internal` | `order.agnello.internal` |
| `sub` (quem é) | `cliente` | `order-service` |
| `aud` (para quem vale) | `order-service` | `catalog-service` |
| `scope` (o que pode) | `carrinho:read` | `catalog:read` |
| `exp` (validade) | 15 minutos | 60 segundos, um token novo por chamada |
| `jti` | identificador único | identificador único |

Cada serviço só aceita tokens com a **própria audiência**. O token do cliente não abre o Catalog Service, e um token de serviço não abre o Order Service: quem rouba um deles não ganha acesso ao resto. O `verify.sh` e os testes conferem isso.

**Assinatura: HS256 com chave compartilhada.** Os dois serviços têm a mesma chave de 384 bits; o Order Service assina e o Catalog Service confere. É a opção mais simples com só dois serviços. Numa arquitetura completa, o **Authentication Service** do documento de arquitetura emitiria os tokens com **RS256/ES256** e publicaria a chave pública num endpoint JWKS: cada serviço guardaria só a chave pública e não teria como forjar tokens. Aqui o Order Service faz o papel do Authentication Service.

## 2. Autenticação implementada

- **Login com HTTP Basic, só em `POST /v1/auth/token`.** O usuário de demonstração é `cliente`; a senha é gerada na primeira subida (seção 3), nunca está no repositório e fica guardada em memória como hash BCrypt. A resposta é um JWT A. A senha não serve em nenhum outro endpoint (403).
- **JWT nas chamadas.** `GET /v1/carrinhos/{id}` exige um JWT A válido. Sem token, ou com token expirado, de outra chave, de outro emissor ou de outra audiência, a resposta é 401.
- **Token de serviço.** O Order Service gera um JWT B a cada chamada ao Catalog Service. O Catalog Service exige o JWT B em `GET /v1/vinhos` e `GET /v1/vinhos/{id}`.
- **Só `/health` é aberto** nos dois serviços, para os healthchecks do Docker. Qualquer rota que não foi liberada explicitamente é negada (`denyAll`).

A validação usa o Spring Security (resource server OAuth 2.0) e a biblioteca Nimbus JOSE. Os serviços continuam sem sessão: cada requisição se autentica sozinha.

## 3. TLS e segredos

Na primeira vez que o ambiente sobe, o job `pki` (em [`pki/init-pki.sh`](pki/init-pki.sh)) gera no volume Docker `agnello-pki`:

| Arquivo | O que é | Permissão |
|---|---|---|
| `ca.crt` | certificado da CA interna "Vinheria Agnello Internal CA" (ECDSA P-256) | 0644 |
| `catalog.crt`, `catalog.key` | certificado e chave do Catalog Service, para `catalog.agnello.internal`, `localhost` e `127.0.0.1` | 0644 e 0640 |
| `order.crt`, `order.key` | o mesmo para o Order Service, com `order.agnello.internal` | 0644 e 0640 |
| `jwt.secret` | chave HS256, 384 bits aleatórios em base64 | 0640 |
| `demo-password` | senha do usuário `cliente` | 0640 |

- A **chave privada da CA é descartada** depois de assinar os dois certificados: ninguém consegue emitir outro certificado em que o ambiente confie.
- Os serviços rodam como usuários sem privilégio (uid 10001 e 10002, grupo 10000) e montam o volume como **somente leitura**. Chaves e segredos são legíveis só pelo grupo dos serviços.
- O Order Service confere o certificado do Catalog Service contra `ca.crt` e contra o nome `catalog.agnello.internal`, resolvido pelo dnsmasq. Um servidor falso no lugar do Catalog Service não teria um certificado válido para esse nome.
- **Só TLS 1.3 e 1.2.** As portas 8081 e 8082 não falam HTTP puro: uma requisição sem TLS recebe 400 do Tomcat antes de chegar na aplicação.
- Nada disso vai para o Git. O volume sobrevive a `down`; `down -v` apaga tudo e a próxima subida gera tudo de novo (outra CA, outra chave JWT, outra senha).

Para chamar os serviços da máquina host:

```bash
docker exec agnello-order cat /run/agnello/ca.crt > infra/security/ca.crt      # ignorado pelo Git
SENHA=$(docker exec agnello-order cat /run/agnello/demo-password)
TOKEN=$(curl -s --cacert infra/security/ca.crt -u "cliente:$SENHA" -X POST https://localhost:8082/v1/auth/token \
  | sed 's/.*"access_token":"\([^"]*\)".*/\1/')
curl -s --cacert infra/security/ca.crt -H "Authorization: Bearer $TOKEN" https://localhost:8082/v1/carrinhos/1
```

No Windows, o `curl` do Git Bash usa o Schannel, que tenta checar revogação e não consegue com uma CA privada: acrescente `--ssl-no-revoke` aos dois `curl`.

## 4. Demonstração com Wireshark

O Docker Desktop roda a rede `agnello-net` dentro de uma VM, então o Wireshark do Windows não enxerga esse tráfego diretamente. Por isso a captura é feita **dentro da rede**: um contêiner com `tcpdump` entra no namespace de rede do Order Service e grava tudo o que ele envia e recebe (o cliente chamando o Order Service e o Order Service chamando o Catalog Service). O arquivo `.pcap` é aberto depois no Wireshark.

```bash
docker compose -f infra/docker-compose.yml up -d --build
infra/security/capture.sh
```

O script roda o mesmo cenário duas vezes e no fim deixa o ambiente em HTTPS:

1. **Sem segurança.** Sobe os serviços com [`../docker-compose.insecure.yml`](../docker-compose.insecure.yml) (mesmos serviços e mesmos JWTs, mas HTTP puro), faz o login e lê o Carrinho 1. Resultado: `captures/inseguro.pcap`.
2. **Com segurança.** Volta ao `docker-compose.yml` (HTTPS) e repete. Resultado: `captures/seguro.pcap`.

Saída do script (a senha, os tokens e os horários mudam a cada ambiente):

```
== 1/2  SEM SEGURANÇA: HTTP puro (docker-compose.insecure.yml)
  login: 200, Carrinho 1: 200

== O que o Wireshark lê em inseguro.pcap (filtro: http.request)
  172.28.0.3 172.28.0.12 POST /v1/auth/token
  172.28.0.3 172.28.0.12 GET /v1/carrinhos/1
  172.28.0.12 172.28.0.11 GET /v1/vinhos/1
  172.28.0.12 172.28.0.11 GET /v1/vinhos/4

== Senha do cliente, em claro (campo http.authbasic)
  cliente:2qgncwDfs92qlDtYGDgpQeZ

== Tokens JWT, em claro (campo http.authorization)
  172.28.0.12	Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlIiwiYXVkIjoib3JkZXItc2VydmljZSIs...
  172.28.0.11	Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvcmRlci1zZXJ2aWNlIiwiYXVkIjoiY2F0YWxvZy1z...

== Dados de negócio, em claro (resposta do Catalog Service: nome e preço)
  {"idVinho":1,"nome":"Cabernet Sauvignon Reserva","tipo":"tinto","regiao":"Vale dos Vinhedos","safra":2020,"preco":89.90,"ativo":true}

== Ataque: reutilizar o token de serviço lido na captura (replay)
  claims do token roubado: {"sub":"order-service","aud":"catalog-service","scope":"catalog:read","iss":"order.agnello.internal",...}
  atacante -> GET http://catalog.agnello.internal:8081/v1/vinhos com o token roubado: HTTP 200

== 2/2  COM SEGURANÇA: HTTPS (TLS 1.3) com o mesmo JWT (docker-compose.yml)
  login: 200, Carrinho 1: 200

== O que o Wireshark lê em seguro.pcap
  requisições HTTP legíveis: 0
  ocorrências de "Bearer", "eyJ" (início de um JWT) ou "Cabernet" nos bytes: 0
  handshakes TLS (filtro: tls.handshake), servidor pedido (SNI) e versão negociada:
    ServerHello 172.28.0.11 172.28.0.12 TLS 1.3
    ServerHello 172.28.0.12 172.28.0.3 TLS 1.3
    ClientHello 172.28.0.12 172.28.0.11 catalog.agnello.internal
    ClientHello 172.28.0.3 172.28.0.12 order.agnello.internal
  pacotes com registros "Application Data" (conteúdo cifrado): 25

== Ataque de novo: o mesmo pedido em HTTP puro contra o Catalog Service em HTTPS
  Bad Request This combination of host and port requires TLS.  (HTTP 400)
```

**O que isso prova.**

- **Sem TLS, autenticação não é segurança.** A senha do Basic é só base64: o Wireshark mostra `cliente:senha` decodificado. O JWT também é só base64url: qualquer um lê os claims. O JWT impede que alguém **forje** um token, mas não impede que alguém **copie** um que passou pela rede. O script faz exatamente isso: lê o token de serviço na captura e chama o Catalog Service se passando pelo Order Service (HTTP 200).
- **Com TLS, o mesmo tráfego vira ruído.** Só aparecem o handshake e os registros "Application Data", cifrados. Os bytes da captura não contêm nem `Bearer`, nem `eyJ`, nem o nome de um vinho. O que continua visível são os **metadados**: IPs, portas, tamanhos, horários e o nome pedido no SNI (`catalog.agnello.internal`).
- **As camadas se somam.** TLS protege o caminho e prova a identidade do servidor (certificado). JWT prova a identidade de quem chama e limita o que pode fazer (`aud`, `scope`) e por quanto tempo (`exp`): um token vazado de outro jeito vale no máximo 60 segundos e para um serviço só.

### Prints para o trabalho no Wireshark

Abra `infra/security/captures/inseguro.pcap` e `seguro.pcap` no Wireshark. As portas 8081 e 8082 não são as portas padrão de HTTP e HTTPS, então diga ao Wireshark como decodificá-las: **Analyze > Decode As…**, adicione `TCP port 8081` e `TCP port 8082`, com **HTTP** para `inseguro.pcap` e **TLS** para `seguro.pcap`.

| Print | Arquivo | Filtro | O que mostrar |
|---|---|---|---|
| Senha em claro | `inseguro.pcap` | `http.authbasic` | Abra o pacote `POST /v1/auth/token` e expanda *Authorization*: o Wireshark mostra a credencial decodificada |
| Token em claro | `inseguro.pcap` | `http.authorization contains "Bearer"` | O JWT inteiro no cabeçalho, para qualquer um copiar |
| Dados em claro | `inseguro.pcap` | `http.response` | Botão direito > *Follow > HTTP Stream* numa resposta do Catalog Service: o JSON com preço |
| Handshake | `seguro.pcap` | `tls.handshake` | *Client Hello* com o SNI `catalog.agnello.internal`; *Server Hello* com TLS 1.3 |
| Conteúdo cifrado | `seguro.pcap` | `tls` | *Follow > TCP Stream*: só bytes sem sentido; nenhum `Bearer` |

## 5. DevSecOps

O que este item já põe em prática:

- **Segredos fora do código.** Nenhuma senha, chave ou certificado está no repositório: tudo é gerado no ambiente, com permissões restritas. O `.gitignore` cobre capturas e cópias locais da CA. Comparar com uma chave fixa no `application.properties`: ela iria para o Git, para cada clone e para o histórico, e não teria como ser trocada sem um novo commit.
- **Segurança testada a cada build.** Os testes de unidade (`mvn test` em cada serviço) cobrem os casos de recusa: sem token, token expirado, de outra chave, de outro emissor, de outra audiência, senha errada, senha usada fora do login. O `verify.sh` confere o mesmo no ambiente real, por DNS e TLS. Os dois já rodam no [`Jenkinsfile`](../../Jenkinsfile) do item 2 (estágios 2 e 6): um teste de segurança que falha impede o deploy, e um deploy que não passa no `verify.sh` deixa o build vermelho.
- **Menor privilégio.** Contêineres com usuário sem privilégio, volume de segredos somente leitura, portas publicadas só no loopback, rotas negadas por padrão, tokens com escopo, audiência e validade curtos.
- **Fins de linha LF garantidos** pelo `.gitattributes`: um clone no Windows com `core.autocrlf=true` não quebra os scripts nem o `dnsmasq.conf` dentro dos contêineres.

Próximos estágios de segurança propostos para o `Jenkinsfile`, antes do deploy (ainda não implementados):

```groovy
stage('Varredura de segredos') {
    // Procura chaves, senhas e tokens em todo o histórico do Git.
    steps { sh 'gitleaks detect --source . --no-banner' }
}
stage('Dependências e imagens') {
    steps {
        // Falha o build com CVE alta ou crítica nas dependências Maven ou nas imagens.
        sh 'trivy fs --exit-code 1 --severity HIGH,CRITICAL .'
        sh 'trivy image --exit-code 1 --severity HIGH,CRITICAL agnello-catalog agnello-order'
    }
}
```

`gitleaks` e `trivy` entrariam na imagem do Jenkins ([`../jenkins/Dockerfile`](../jenkins/Dockerfile)). Rodá-los com `docker run -v "$PWD:..."` não funciona aqui: o Docker é o do host, que não enxerga o workspace de dentro do contêiner do Jenkins.

## 6. Limitações e próximos passos

- **mTLS** (o Catalog Service exigir certificado do Order Service) daria uma segunda prova de identidade, além do JWT.
- **RS256 com JWKS** e um Authentication Service de verdade (seção 1), em vez da chave HS256 compartilhada.
- **Rotação** de chaves e certificados, e **revogação** de tokens antes do `exp` (lista de `jti` revogados).
- **API Gateway** na frente dos serviços, com rate limit no login.
- O usuário `cliente` é único e em memória: o User Service da arquitetura guardaria os usuários e os hashes.

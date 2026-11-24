# Plano de rede

Rede `agnello-net`, 172.28.0.0/24, gateway 172.28.0.1, zona `agnello.internal`.

## Hosts fixos

Esta tabela é a referência de endereços, nomes e MACs dos hosts fixos. Os arquivos que a repetem devem segui-la e mudar junto com ela:

- `dnsmasq/dnsmasq.conf`: registros DNS (`host-record`) e reservas na tabela de leases (`dhcp-host`).
- `docker-compose.yml`: `ipv4_address` e `mac_address` de cada host fixo.
- `verify.sh`: os endereços e nomes esperados.

| Host | Nome DNS | Endereço | MAC |
|---|---|---|---|
| dnsmasq (DHCP e DNS) | sem registro (é o próprio servidor) | 172.28.0.2 | fora da tabela de leases |
| Catalog Service | `catalog.agnello.internal` | 172.28.0.11 | 02:42:ac:1c:00:0b |
| Order Service | `order.agnello.internal` | 172.28.0.12 | 02:42:ac:1c:00:0c |

Os MACs são os que o Docker derivaria de cada endereço (`02:42` seguido dos quatro bytes do endereço em hexadecimal). Fixá-los com `mac_address` faz a tabela de leases e os contêineres concordarem por contrato, não por coincidência.

## Pool DHCP

Clientes dinâmicos (o probe) recebem de 172.28.0.100 a 172.28.0.199, com leases de 12 horas. Opção de roteador 172.28.0.1 e opção de servidor DNS 172.28.0.2. Os microsserviços mantêm os endereços fixos do Docker; as reservas acima apenas os espelham ("DHCP simulado").

## Portas

| Host | Porta | Publicada na máquina host |
|---|---|---|
| Catalog Service | 8081, HTTPS | sim, `127.0.0.1:8081` |
| Order Service | 8082, HTTPS | sim, `127.0.0.1:8082` |
| dnsmasq (DNS 53, DHCP 67) | | não |

As portas dos microsserviços são publicadas só no loopback (`127.0.0.1`): mesmo com TLS e JWT (item 3), outras máquinas da rede não devem alcançá-las.

O tráfego entre os microsserviços é HTTPS (TLS 1.3) com JWT; veja [`security/README.md`](security/README.md). O Order Service encontra o Catalog Service pela configuração `CATALOG_URL`, `https://catalog.agnello.internal:8081`: o nome DNS, resolvido pelo dnsmasq, que também é o nome no certificado. Só o `docker-compose.insecure.yml` volta ao HTTP puro, para a comparação no Wireshark.

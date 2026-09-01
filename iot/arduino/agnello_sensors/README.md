# ESP32: monitoramento do armazenamento

## Objetivo

O ESP32 lê dois sensores:

- DHT22: temperatura e umidade. Conta como um sensor combinado.
- Fotoresistor: luminosidade relativa de 0 a 100, não lux.

As leituras ocorrem a cada 4000 ms e são enviadas por serial e MQTT.

## Wokwi

1. Abra ou crie um projeto ESP32.
2. Copie `agnello_sensors.ino`, `diagram.json` e `libraries.txt`.
3. Mantenha `WIFI_SSID` como `Wokwi-GUEST` e `WIFI_PASSWORD` vazio.
4. Confirme `CLASS_ID = "3esoa"` e `GROUP_ID = "agnello"`.
5. Inicie a simulação e abra o monitor serial em `115200 baud`.

O Wokwi envia os dados ao dashboard por MQTT. Altere os controles dos sensores para testar novas leituras.

Exemplo serial:

```json
{"temperature":22.00,"humidity":55.00,"luminosity":48,"dht_status":"OK"}
```

## Placa real

1. Instale as bibliotecas de `libraries.txt`.
2. Monte os sensores conforme a tabela.
3. Configure `WIFI_SSID`, `WIFI_PASSWORD` e `MQTT_HOST`.
4. Grave o programa e abra o monitor serial em `115200 baud`.

## MQTT

```text
Fiap/iot/3esoa/agnello/dht22
Fiap/iot/3esoa/agnello/photoresistor
```

Payloads:

```json
{"temperature":22.00,"humidity":55.00}
```

```json
{"luminosity":48}
```

O DHT22 só publica quando as duas leituras são válidas. O broker inicial é `broker.hivemq.com:1883` e deve ser usado apenas em testes.

## Ligações

| Componente | ESP32 |
| --- | --- |
| DHT22 VCC / GND / DATA | 3V3 / GND / GPIO 15 (D15) |
| Fotoresistor VCC / GND / AO | 3V3 / GND / GPIO 34 (D34) |

Use um resistor de 10 kOhm entre DATA do DHT22 e 3V3. Não aplique 5 V aos pinos de sinal.

Se o DHT22 falhar, `temperature` e `humidity` serão `null`. `dht_status` pode ser `OK`, `TIMEOUT` ou `CHECKSUM`.

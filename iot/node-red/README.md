# Node-RED: monitoramento

O arquivo `flows.json` usa o **FlowFuse Dashboard 2.0** para exibir as leituras MQTT do ESP32.

## Instalação

```bash
cd iot/node-red
npm install
npm start
```

Abra o editor em `http://localhost:1880` e o painel em `http://localhost:1880/dashboard/`.

O projeto usa:

- `@flowfuse/node-red-dashboard`
- `node-red-node-serialport`

## Configuração

```text
Broker: broker.hivemq.com:1883
Tópico: Fiap/iot/3esoa/agnello/+
Porta serial: /dev/ttyUSB0
Velocidade: 115200
```

Se `CLASS_ID` ou `GROUP_ID` mudarem no sketch, atualize o tópico MQTT para:

```text
Fiap/iot/<turma>/<grupo>/+
```

## Wokwi

1. Inicie a simulação.
2. Confirme `dht_status = "OK"` no monitor serial.
3. Abra o painel em `http://localhost:1880/dashboard/`.

O dashboard recebe dados por MQTT. O monitor serial do Wokwi fica no navegador e não é uma porta local do Node-RED.

## ESP32 físico

1. Conecte a placa por USB.
2. Selecione a porta no nó **Entrada serial do ESP32**.
3. Mantenha `115200 baud`.
4. Clique em **Deploy** e verifique a **Depuração do JSON serial**.

## Validação

- O nó MQTT está conectado e assina o tópico correto.
- A **Depuração MQTT** mostra os payloads JSON.
- Os indicadores exibem temperatura, umidade e luminosidade.
- Os gráficos mostram uma hora de histórico.

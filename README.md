# vinheria-agnelo

## Instruções

- Para utilizar o `DatabaseConfig`:

```java
import java.sql.Connection;
import br.com.vinheriaagnello.config.DatabaseConfig;

Connection conn = DatabaseConfig.getDataSource().getConnection();
```

## IoT

A solução IoT para monitorar as condições de armazenamento dos vinhos está organizada em duas etapas:

- Programa do ESP32, sensores e comunicação MQTT em [`iot/arduino/agnello_sensors`](iot/arduino/agnello_sensors).
- Fluxo de monitoramento e dashboard no Node-RED em [`iot/node-red`](iot/node-red).

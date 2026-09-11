#include <Arduino.h>
#include <DHTesp.h>
#include <PubSubClient.h>
#include <WiFi.h>

// ESP32 pins used by the Wokwi diagram and the physical prototype.
#define DHT_PIN 15
#define LDR_PIN 34

constexpr unsigned long SAMPLE_INTERVAL_MS = 4000UL;
constexpr unsigned long WIFI_RETRY_INTERVAL_MS = 10000UL;
constexpr unsigned long MQTT_RETRY_INTERVAL_MS = 5000UL;

// Replace these values before using physical hardware or a private broker.
const char* WIFI_SSID = "Wokwi-GUEST";
const char* WIFI_PASSWORD = "";
const char* MQTT_HOST = "broker.hivemq.com";
const uint16_t MQTT_PORT = 1883;
const char* MQTT_USERNAME = "";
const char* MQTT_PASSWORD = "";
const char* CLASS_ID = "3esoa";
const char* GROUP_ID = "agnello";

DHTesp dht;
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

char dhtTopic[96];
char ldrTopic[96];
String mqttClientId;
unsigned long lastSampleAt = 0;
unsigned long nextWifiAttemptAt = 0;
unsigned long nextMqttAttemptAt = 0;

void buildTopics() {
  snprintf(dhtTopic, sizeof(dhtTopic), "Fiap/iot/%s/%s/dht22", CLASS_ID, GROUP_ID);
  snprintf(ldrTopic, sizeof(ldrTopic), "Fiap/iot/%s/%s/photoresistor", CLASS_ID, GROUP_ID);
}

void maintainWiFi(unsigned long now) {
  if (WiFi.status() == WL_CONNECTED) {
    return;
  }

  if (mqttClient.connected()) {
    mqttClient.disconnect();
  }

  if (now < nextWifiAttemptAt) {
    return;
  }

  WiFi.disconnect();
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  nextWifiAttemptAt = now + WIFI_RETRY_INTERVAL_MS;
}

void maintainMqtt(unsigned long now) {
  if (WiFi.status() != WL_CONNECTED || mqttClient.connected() || now < nextMqttAttemptAt) {
    return;
  }

  nextMqttAttemptAt = now + MQTT_RETRY_INTERVAL_MS;

  if (strlen(MQTT_USERNAME) == 0) {
    mqttClient.connect(mqttClientId.c_str());
    return;
  }

  mqttClient.connect(mqttClientId.c_str(), MQTT_USERNAME, MQTT_PASSWORD);
}

void publishSensorData(float temperature, float humidity, int luminosity) {
  if (!mqttClient.connected()) {
    return;
  }

  if (!isnan(temperature) && !isnan(humidity)) {
    char dhtPayload[128];
    snprintf(
      dhtPayload,
      sizeof(dhtPayload),
      "{\"temperature\":%.2f,\"humidity\":%.2f}",
      temperature,
      humidity
    );
    mqttClient.publish(dhtTopic, dhtPayload);
  }

  char ldrPayload[64];
  snprintf(ldrPayload, sizeof(ldrPayload), "{\"luminosity\":%d}", luminosity);
  mqttClient.publish(ldrTopic, ldrPayload);
}

void sampleSensors() {
  const TempAndHumidity dhtReading = dht.getTempAndHumidity();
  const float temperature = dhtReading.temperature;
  const float humidity = dhtReading.humidity;
  const char* dhtStatus = dht.getStatusString();
  const int rawLuminosity = analogRead(LDR_PIN);
  const int luminosity = constrain(map(rawLuminosity, 4095, 0, 0, 100), 0, 100);

  // Keep the serial channel parseable by Node-RED: one JSON object per line.
  char serialPayload[192];
  if (isnan(temperature) || isnan(humidity)) {
    snprintf(
      serialPayload,
      sizeof(serialPayload),
      "{\"temperature\":null,\"humidity\":null,\"luminosity\":%d,\"dht_status\":\"%s\"}",
      luminosity,
      dhtStatus
    );
  } else {
    snprintf(
      serialPayload,
      sizeof(serialPayload),
      "{\"temperature\":%.2f,\"humidity\":%.2f,\"luminosity\":%d,\"dht_status\":\"%s\"}",
      temperature,
      humidity,
      luminosity,
      dhtStatus
    );
  }

  Serial.println(serialPayload);
  publishSensorData(temperature, humidity, luminosity);
}

void setup() {
  Serial.begin(115200);
  dht.setup(DHT_PIN, DHTesp::DHT22);
  analogReadResolution(12);

  buildTopics();
  mqttClient.setServer(MQTT_HOST, MQTT_PORT);
  mqttClientId = String("agnello-") + String((uint32_t)(ESP.getEfuseMac() & 0xFFFFFFFFULL), HEX);

  WiFi.mode(WIFI_STA);
  WiFi.setAutoReconnect(true);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  nextWifiAttemptAt = millis() + WIFI_RETRY_INTERVAL_MS;

  // Allow the DHT22 to settle before the first sample.
  lastSampleAt = millis();
}

void loop() {
  const unsigned long now = millis();

  maintainWiFi(now);
  maintainMqtt(now);

  if (mqttClient.connected()) {
    mqttClient.loop();
  }

  if (now - lastSampleAt >= SAMPLE_INTERVAL_MS) {
    lastSampleAt = now;
    sampleSensors();
  }
}

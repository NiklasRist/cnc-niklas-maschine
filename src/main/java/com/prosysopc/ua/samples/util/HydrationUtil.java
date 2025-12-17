package com.prosysopc.ua.samples.util;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.Properties;


public class HydrationUtil {

    private static final String DEFAULT_MQTT = "tcp://mqtt_broker:1883";
    private static final String DEFAULT_KAFKA = "redpanda_broker:9092";
    private static final String DEFAULT_REDIS_HOST = "redis_container";

    private static final String MQTT_TOPIC_IN = "cnc_machine/data";
    private static final String KAFKA_TOPIC_OUT = "cnc_data";

    public static void main(String[] args) {
        String mqttBroker = System.getenv().getOrDefault("MQTT_BROKER", DEFAULT_MQTT);
        String kafkaBroker = System.getenv().getOrDefault("KAFKA_BROKER", DEFAULT_KAFKA);
        String redisHost = System.getenv().getOrDefault("REDIS_HOST", DEFAULT_REDIS_HOST);
        int redisPort = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));

        try {
            MqttClient mqttClient = new MqttClient(mqttBroker, MqttClient.generateClientId());
            mqttClient.connect();
            System.out.println("Connected to MQTT: " + mqttBroker);

            Properties kafkaProps = new Properties();
            kafkaProps.put("bootstrap.servers", kafkaBroker);
            kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            Producer<String, String> producer = new KafkaProducer<>(kafkaProps);

            Jedis jedis = new Jedis(redisHost, redisPort);
            System.out.println("Connected to Redis: " + redisHost + ":" + redisPort);

            mqttClient.subscribe(MQTT_TOPIC_IN, (topic, msg) -> {
                try {
                    String payload = new String(msg.getPayload());
                    JSONObject json = new JSONObject(payload);

                    String ctxString = jedis.get("machine:energy:context");
                    JSONObject context = (ctxString != null) ? new JSONObject(ctxString) : new JSONObject();
                    json.put("context", context);

                    producer.send(new ProducerRecord<>(KAFKA_TOPIC_OUT, json.toString()));
                    System.out.println("Enriched & sent to Kafka: " + json);

                } catch (Exception ex) {
                    System.err.println("Error processing MQTT message: " + ex.getMessage());
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

package com.prosysopc.ua.samples.util;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.Random;

public class MqttOpcUaUtil {

    private static final String DEFAULT_BROKER = "tcp://mqtt_broker:1883";
    private static final String TOPIC = "cnc_machine/data";

    private static final String MACHINE = "CNC-ENERGY-01";

    private final Random random = new Random();

    public static void main(String[] args) {
        String brokerUrl = System.getenv().getOrDefault("MQTT_BROKER", DEFAULT_BROKER);
        MqttOpcUaUtil agent = new MqttOpcUaUtil();
        agent.run(brokerUrl);
    }

    private void run(String brokerUrl) {
        try {
            MqttClient client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            client.connect();
            System.out.println("Connected to MQTT broker: " + brokerUrl);

            int iteration = 0;

            while (true) {
                long now = System.currentTimeMillis();

                JSONObject energyMsg = buildEnergySample(now);
                publish(client, energyMsg);


                if (iteration % 4 == 0) {
                    JSONObject maintMsg = buildMaintenanceSample(now);
                    publish(client, maintMsg);
                }


                if (random.nextDouble() < 0.25) {
                    JSONObject anomalyMsg = buildAnomalyEvent(now);
                    publish(client, anomalyMsg);
                }

                iteration++;
                Thread.sleep(15_000);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publish(MqttClient client, JSONObject msg) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(msg.toString().getBytes());
        mqttMessage.setQos(1);
        client.publish(TOPIC, mqttMessage);
        System.out.println("Sent " + msg.optString("event") + ": " + msg);
    }

    // -------------------------------------------------------------------------
    // Message Builder
    // -------------------------------------------------------------------------

    private JSONObject baseEvent(long timestamp, String eventType) {
        JSONObject json = new JSONObject();
        json.put("machine", MACHINE);
        json.put("timestamp", timestamp);
        json.put("event", eventType);
        return json;
    }


    private JSONObject buildEnergySample(long timestamp) {
        JSONObject json = baseEvent(timestamp, "ENERGY_SAMPLE");

        double energyKwh = 0.02 + random.nextDouble() * 0.06;   // 0.02 - 0.08 kWh in 15s
        double currentA = 8.0 + random.nextDouble() * 15.0;     // 8 - 23 A
        double voltageV = 380 + random.nextDouble() * 40.0;     // 380 - 420 V
        double powerFactor = 0.8 + random.nextDouble() * 0.18;  // 0.8 - 0.98

        json.put("energyConsumptionKwh", energyKwh);
        json.put("currentDrawA", currentA);
        json.put("voltageV", voltageV);
        json.put("powerFactor", powerFactor);

        return json;
    }


    private JSONObject buildMaintenanceSample(long timestamp) {
        JSONObject json = baseEvent(timestamp, "MAINTENANCE_SAMPLE");

        double maintenanceDueHours = 10.0 + random.nextDouble() * 80.0;
        double toolUsageMinutes = 50.0 + random.nextDouble() * 400.0;
        double vibration = 0.05 + random.nextDouble() * 0.20;
        double bearingTemp = 40.0 + random.nextDouble() * 30.0;

        json.put("maintenanceDueHours", maintenanceDueHours);
        json.put("toolUsageMinutes", toolUsageMinutes);
        json.put("spindleVibrationMm", vibration);
        json.put("bearingTemperature", bearingTemp);

        return json;
    }


    private JSONObject buildAnomalyEvent(long timestamp) {
        JSONObject json = baseEvent(timestamp, "ANOMALY_DETECTED");

        boolean vibrationSpike = random.nextBoolean();
        if (vibrationSpike) {
            json.put("anomalyType", "VIBRATION_SPIKE");
            json.put("anomalyValue", 0.30 + random.nextDouble() * 0.30);
        } else {
            json.put("anomalyType", "POWER_SPIKE");
            json.put("anomalyValue", 1.1 + random.nextDouble() * 0.4);
        }

        return json;
    }
}

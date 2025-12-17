package com.prosysopc.ua.samples.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;

import java.sql.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class TimescaleUtil {

    private static final String JDBC_URL  = "jdbc:postgresql://timescaledb:5432/mydb";
    private static final String JDBC_USER = "admin";
    private static final String JDBC_PWD  = "passwort123";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PWD)) {
            System.out.println("Connected to TimescaleDB");

            initSchema(conn);

            KafkaConsumer<String, String> consumer = createConsumer();
            consumer.subscribe(Collections.singletonList("cnc_data"));
            System.out.println("Subscribed to Kafka topic: cnc_data");

            runLoop(conn, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    //  Schema / Continuous Aggregates
    // -------------------------------------------------------------------------

    private static void initSchema(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {


            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS cnc_energy_events (
                        time TIMESTAMPTZ NOT NULL,
                        machine TEXT NOT NULL,
                        event_type TEXT NOT NULL CHECK (event_type IN (
                            'ENERGY_SAMPLE',
                            'MAINTENANCE_SAMPLE',
                            'ANOMALY_DETECTED'
                        )),
                        energy_kwh DOUBLE PRECISION,
                        current_a DOUBLE PRECISION,
                        voltage_v DOUBLE PRECISION,
                        power_factor DOUBLE PRECISION,
                        maintenance_due_hours DOUBLE PRECISION,
                        tool_usage_minutes DOUBLE PRECISION,
                        spindle_vibration_mm DOUBLE PRECISION,
                        bearing_temp DOUBLE PRECISION,
                        anomaly_type TEXT,
                        anomaly_value DOUBLE PRECISION,
                        plant TEXT,
                        energy_zone TEXT,
                        maintenance_team TEXT,
                        maintenance_interval TEXT,
                        machine_category TEXT,
                        PRIMARY KEY (time, machine, event_type)
                    );
                    """);

            st.execute("SELECT create_hypertable('cnc_energy_events', 'time', if_not_exists => TRUE);");

            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_energy_time_desc ON cnc_energy_events (time DESC);");


            st.execute("""
                    SELECT add_retention_policy(
                        'cnc_energy_events',
                        INTERVAL '90 days',
                        if_not_exists => TRUE
                    );
                    """);

            System.out.println("Base table & hypertable ready.");


            st.executeUpdate("""
                    CREATE MATERIALIZED VIEW IF NOT EXISTS hourly_energy_usage
                    WITH (timescaledb.continuous) AS
                    SELECT
                        time_bucket('1 hour', time) AS bucket,
                        machine,
                        AVG(energy_kwh) AS avg_kwh,
                        AVG(current_a) AS avg_current
                    FROM cnc_energy_events
                    WHERE event_type = 'ENERGY_SAMPLE'
                    GROUP BY bucket, machine
                    WITH NO DATA;
                    """);
            st.execute("CALL refresh_continuous_aggregate('hourly_energy_usage', NULL, NULL);");


            st.executeUpdate("""
                    CREATE MATERIALIZED VIEW IF NOT EXISTS daily_maintenance_trend
                    WITH (timescaledb.continuous) AS
                    SELECT
                        time_bucket('1 day', time) AS bucket,
                        machine,
                        AVG(spindle_vibration_mm) AS avg_vibration,
                        AVG(bearing_temp) AS avg_temperature
                    FROM cnc_energy_events
                    WHERE event_type = 'MAINTENANCE_SAMPLE'
                    GROUP BY bucket, machine
                    WITH NO DATA;
                    """);
            st.execute("CALL refresh_continuous_aggregate('daily_maintenance_trend', NULL, NULL);");


            st.execute("""
                    SELECT add_continuous_aggregate_policy(
                        'hourly_energy_usage',
                        start_offset => INTERVAL '7 days',
                        end_offset   => INTERVAL '3 minutes',
                        schedule_interval => INTERVAL '3 minutes'
                    );
                    """);

            st.execute("""
                    SELECT add_continuous_aggregate_policy(
                        'daily_maintenance_trend',
                        start_offset => INTERVAL '60 days',
                        end_offset   => INTERVAL '5 minutes',
                        schedule_interval => INTERVAL '5 minutes'
                    );
                    """);

            System.out.println("Continuous aggregates ready.");
        }
    }

    // -------------------------------------------------------------------------
    //  Kafka Consumer
    // -------------------------------------------------------------------------

    private static KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "redpanda_broker:9092");
        props.put("group.id", "timescale-agent-group-v3");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return new KafkaConsumer<>(props);
    }

    private static void runLoop(Connection conn, KafkaConsumer<String, String> consumer) {
        System.out.println("Start polling loop ...");

        final String insertSql = """
                INSERT INTO cnc_energy_events (
                    time,
                    machine,
                    event_type,
                    energy_kwh,
                    current_a,
                    voltage_v,
                    power_factor,
                    maintenance_due_hours,
                    tool_usage_minutes,
                    spindle_vibration_mm,
                    bearing_temp,
                    anomaly_type,
                    anomaly_value,
                    plant,
                    energy_zone,
                    maintenance_team,
                    maintenance_interval,
                    machine_category
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
            if (records.isEmpty()) {
                continue;
            }

            for (ConsumerRecord<String, String> record : records) {
                try {
                    JSONObject json = new JSONObject(record.value());

                    long ts = json.optLong("timestamp", System.currentTimeMillis());
                    String machine = json.optString("machine", "unknown");
                    String eventType = json.optString("event", "UNKNOWN");

                    Double energyKwh = null;
                    Double currentA = null;
                    Double voltageV = null;
                    Double powerFactor = null;
                    Double maintDue = null;
                    Double toolUsage = null;
                    Double vibration = null;
                    Double bearingTemp = null;
                    String anomalyType = null;
                    Double anomalyValue = null;

                    if ("ENERGY_SAMPLE".equals(eventType)) {
                        energyKwh = json.has("energyConsumptionKwh") ? json.optDouble("energyConsumptionKwh") : null;
                        currentA = json.has("currentDrawA") ? json.optDouble("currentDrawA") : null;
                        voltageV = json.has("voltageV") ? json.optDouble("voltageV") : null;
                        powerFactor = json.has("powerFactor") ? json.optDouble("powerFactor") : null;
                    } else if ("MAINTENANCE_SAMPLE".equals(eventType)) {
                        maintDue = json.has("maintenanceDueHours") ? json.optDouble("maintenanceDueHours") : null;
                        toolUsage = json.has("toolUsageMinutes") ? json.optDouble("toolUsageMinutes") : null;
                        vibration = json.has("spindleVibrationMm") ? json.optDouble("spindleVibrationMm") : null;
                        bearingTemp = json.has("bearingTemperature") ? json.optDouble("bearingTemperature") : null;
                    } else if ("ANOMALY_DETECTED".equals(eventType)) {
                        anomalyType = json.optString("anomalyType", null);
                        anomalyValue = json.has("anomalyValue") ? json.optDouble("anomalyValue") : null;
                    }

                    JSONObject context = json.optJSONObject("context");
                    String plant = context != null ? context.optString("plant", null) : null;
                    String energyZone = context != null ? context.optString("energyZone", null) : null;
                    String maintenanceTeam  = context != null ? context.optString("maintenanceTeam", null) : null;
                    String maintenanceInterval  = context != null ? context.optString("maintenanceInterval", null) : null;
                    String machineCategory = context != null ?  context.optString("machineCategory", null) : null;

                    try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                        ps.setTimestamp(1, new Timestamp(ts));
                        ps.setString(2, machine);
                        ps.setString(3, eventType);

                        setNullableDouble(ps, 4, energyKwh);
                        setNullableDouble(ps, 5, currentA);
                        setNullableDouble(ps, 6, voltageV);
                        setNullableDouble(ps, 7, powerFactor);
                        setNullableDouble(ps, 8, maintDue);
                        setNullableDouble(ps, 9, toolUsage);
                        setNullableDouble(ps, 10, vibration);
                        setNullableDouble(ps, 11, bearingTemp);

                        if (anomalyType != null) ps.setString(12, anomalyType); else ps.setNull(12, Types.VARCHAR);
                        setNullableDouble(ps, 13, anomalyValue);

                        if (plant != null) ps.setString(14, plant); else ps.setNull(14, Types.VARCHAR);
                        if (energyZone  != null) ps.setString(15, energyZone ); else ps.setNull(15, Types.VARCHAR);
                        if (maintenanceTeam  != null) ps.setString(16, maintenanceTeam ); else ps.setNull(16, Types.VARCHAR);
                        if (maintenanceInterval  != null) ps.setString(17, maintenanceInterval ); else ps.setNull(17, Types.VARCHAR);
                        if (machineCategory  != null) ps.setString(18, machineCategory); else ps.setNull(18, Types.VARCHAR);

                        ps.executeUpdate();
                    }

                    System.out.printf("Inserted %s event for machine %s%n", eventType, machine);

                } catch (Exception e) {
                    System.err.println("Error processing record: " + e.getMessage());
                }
            }
        }
    }

    private static void setNullableDouble(PreparedStatement ps, int index, Double value) throws SQLException {
        if (value != null) {
            ps.setDouble(index, value);
        } else {
            ps.setNull(index, Types.DOUBLE);
        }
    }
}

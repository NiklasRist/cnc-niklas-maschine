# Advanced Data Management System - Technical Documentation

## Table of Contents

1. [System Overview](#system-overview)
2. [Architecture](#architecture)
3. [Components](#components)
4. [Data Flow](#data-flow)
5. [Message Formats](#message-formats)
6. [Database Schema](#database-schema)
7. [Configuration](#configuration)
8. [Development Guide](#development-guide)
9. [Deployment Architecture](#deployment-architecture)
10. [Protocols and APIs](#protocols-and-apis)

---

## System Overview

The Advanced Data Management System is an industrial IoT platform designed for real-time monitoring and analysis of CNC machine data. It implements a complete data pipeline from machine-level telemetry collection through to time-series storage and visualization.

### Key Features

- **OPC UA Integration**: Simulates and interfaces with CNC machines via OPC UA protocol
- **Real-time Messaging**: Uses MQTT for lightweight telemetry transport
- **Data Enrichment**: Enriches messages with contextual metadata from Redis
- **Event Streaming**: Leverages Redpanda (Kafka-compatible) for reliable message queuing
- **Time-Series Storage**: TimescaleDB for efficient storage and querying of time-series data
- **Visualization**: Grafana dashboards for real-time monitoring
- **Monitoring Tools**: Redpanda Console and pgAdmin for system observability

### Technology Stack

- **Language**: Java 21
- **Build Tool**: Maven 3.6+
- **OPC UA SDK**: Prosys OPC UA SDK Evaluation 5.2.2-139
- **Message Broker**: Eclipse Mosquitto (MQTT)
- **Cache**: Redis Stack
- **Streaming**: Redpanda (Kafka-compatible)
- **Database**: TimescaleDB (PostgreSQL extension)
- **Containerization**: Docker & Docker Compose

---

## Architecture

### High-Level Architecture Diagram

```
┌─────────────────┐
│  OPC UA Server  │
│ (SampleConsole) │
└────────┬────────┘
         │
         │ OPC UA Protocol
         │
┌────────▼────────────────────────────────────────┐
│  MqttOpcUaUtil                                  │
│  - Reads OPC UA nodes                           │
│  - Publishes to MQTT                            │
└────────┬────────────────────────────────────────┘
         │
         │ MQTT Topic: cnc_machine/data
         │
┌────────▼────────┐      ┌──────────────┐
│  MQTT Broker    │      │    Redis     │
│ (Mosquitto)     │      │  (Context)   │
└────────┬────────┘      └──────────────┘
         │                      │
         │                      │
┌────────▼──────────────────────▼────────┐
│  HydrationUtil                         │
│  - Subscribes to MQTT                  │
│  - Enriches with Redis context         │
│  - Publishes to Kafka                  │
└────────┬───────────────────────────────┘
         │
         │ Kafka Topic: cnc_data
         │
┌────────▼────────┐
│   Redpanda      │
│  (Kafka API)    │
└────────┬────────┘
         │
         │
┌────────▼────────┐
│ TimescaleUtil   │
│  - Consumes     │
│  - Stores to DB │
└────────┬────────┘
         │
┌────────▼────────┐
│  TimescaleDB    │
│  (Hypertables)  │
└────────┬────────┘
         │
┌────────▼────────┐
│    Grafana      │
│  (Visualization)│
└─────────────────┘
```

### Component Interactions

1. **OPC UA Server** (`SampleConsoleServer`) exposes CNC machine simulation data via OPC UA protocol on port 52520/52599
2. **MqttOpcUaUtil** connects to the OPC UA server and publishes telemetry data to MQTT every 15 seconds
3. **MQTT Broker** (Eclipse Mosquitto) receives messages on topic `cnc_machine/data`
4. **HydrationUtil** subscribes to MQTT, enriches messages with context from Redis, and forwards to Kafka
5. **Redpanda** provides Kafka-compatible message streaming infrastructure
6. **TimescaleUtil** consumes from Kafka and persists data to TimescaleDB
7. **TimescaleDB** stores time-series data with automatic partitioning (hypertables)
8. **Grafana** queries TimescaleDB and visualizes metrics in dashboards

---

## Components

### 1. OPC UA Server (`SampleConsoleServer`)

**Purpose**: Simulates a CNC machine and exposes machine data via OPC UA protocol.

**Location**: `src/main/java/com/prosysopc/ua/samples/server/SampleConsoleServer.java`

**Key Features**:
- Implements OPC UA server using Prosys SDK
- Creates CNC machine address space via `CncNodeManager`
- Exposes machine status, spindle speeds, feed rates, tool life, positions, etc.
- Runs on port 52520 (internal) / 52599 (external)

**Node Manager**: `CncNodeManager` - Manages the CNC machine's address space including:
- Machine status variables
- Spindle and feed rates (target/actual)
- Position coordinates (X, Y, Z)
- Tool life and maintenance data
- Production metrics (good parts, bad parts)
- Alarm conditions

### 2. MQTT OPC UA Utility (`MqttOpcUaUtil`)

**Purpose**: Bridges OPC UA server data to MQTT broker.

**Location**: `src/main/java/com/prosysopc/ua/samples/util/MqttOpcUaUtil.java`

**Key Features**:
- Connects to MQTT broker (default: `tcp://mqtt_broker:1883`)
- Publishes three types of events:
  - **ENERGY_SAMPLE**: Published every 15 seconds with energy consumption metrics
  - **MAINTENANCE_SAMPLE**: Published every 4th iteration (60 seconds)
  - **ANOMALY_DETECTED**: Published randomly (25% probability)
- Uses MQTT QoS 1 (at least once delivery)
- Topic: `cnc_machine/data`

**Configuration**:
- Environment variable: `MQTT_BROKER` (default: `tcp://mqtt_broker:1883`)
- Machine identifier: `CNC-ENERGY-01` (hardcoded)

### 3. Hydration Utility (`HydrationUtil`)

**Purpose**: Enriches MQTT messages with contextual data from Redis and forwards to Kafka.

**Location**: `src/main/java/com/prosysopc/ua/samples/util/HydrationUtil.java`

**Key Features**:
- Subscribes to MQTT topic: `cnc_machine/data`
- Retrieves context from Redis key: `machine:energy:context`
- Merges context into message JSON
- Publishes enriched messages to Kafka topic: `cnc_data`
- Uses Kafka StringSerializer for both key and value

**Context Data** (from Redis):
- `plant`: Plant location identifier
- `energyZone`: Energy grid segment
- `maintenanceTeam`: Assigned maintenance team
- `maintenanceInterval`: Maintenance schedule
- `machineCategory`: Machine classification

**Configuration**:
- Environment variables:
  - `MQTT_BROKER` (default: `tcp://mqtt_broker:1883`)
  - `KAFKA_BROKER` (default: `redpanda_broker:9092`)
  - `REDIS_HOST` (default: `redis_container`)
  - `REDIS_PORT` (default: `6379`)

### 4. Timescale Utility (`TimescaleUtil`)

**Purpose**: Consumes messages from Kafka and stores them in TimescaleDB.

**Location**: `src/main/java/com/prosysopc/ua/samples/util/TimescaleUtil.java`

**Key Features**:
- Kafka consumer subscribing to topic: `cnc_data`
- Consumer group: `timescale-agent-group-v3`
- Creates and manages TimescaleDB schema:
  - Hypertable: `cnc_energy_events`
  - Continuous aggregates:
    - `hourly_energy_usage`: Hourly aggregated energy consumption
    - `daily_maintenance_trend`: Daily maintenance metrics
- Automatic retention policy: 90 days
- Polls Kafka every 3 seconds
- Uses prepared statements for efficient inserts

**Database Connection**:
- JDBC URL: `jdbc:postgresql://timescaledb:5432/mydb`
- Username: `admin`
- Password: `passwort123`

### 5. MQTT Broker (Eclipse Mosquitto)

**Container**: `mqtt_broker`

**Configuration**: `mqtt.conf`

**Ports**:
- `1883`: MQTT protocol (default)
- `8080`: MQTT Control Center (web UI)
- `8883`: MQTT over SSL

### 6. Redis Stack

**Container**: `redis_container`

**Purpose**: Stores contextual metadata for data enrichment.

**Initialization**: The `redis_init` container initializes the context data:
```json
{
  "plant": "Linz Metal Works",
  "energyZone": "Grid-Segment-4",
  "maintenanceTeam": "Team Gamma",
  "maintenanceInterval": "Every 300 hours",
  "machineCategory": "EnergyOptimized"
}
```

**Ports**:
- `6379`: Redis protocol
- `8001`: RedisInsight (web UI)

### 7. Redpanda (Kafka-compatible)

**Container**: `redpanda_broker`

**Purpose**: Provides Kafka-compatible message streaming.

**Ports**:
- `19092`: Kafka API (external)
- `9092`: Kafka API (internal)
- `18081`: Schema Registry (external)
- `18082`: HTTP Proxy (external)
- `19644`: Admin API (external)

**Topics**:
- `cnc_data`: Main data pipeline topic
- `usecase_hydration`: Alternative hydration topic (from Redpanda Connect)

### 8. TimescaleDB

**Container**: `timescaledb`

**Purpose**: Time-series database for storing and querying CNC machine data.

**Version**: PostgreSQL 16 with TimescaleDB extension

**Database**: `mydb`

**Credentials**:
- Username: `admin`
- Password: `passwort123`

**Port**: `5432`

### 9. Grafana

**Container**: `grafana`

**Purpose**: Data visualization and dashboarding.

**Port**: `3000`

**Credentials**:
- Username: `admin`
- Password: `passwort123`

### 10. pgAdmin

**Container**: `pgadmin`

**Purpose**: PostgreSQL database administration tool.

**Port**: `5050`

**Credentials**:
- Email: `admin@example.com`
- Password: `passwort123`

---

## Data Flow

### Complete Data Pipeline

1. **Data Generation** (Every 15 seconds)
   - `MqttOpcUaUtil` generates simulated CNC machine data
   - Creates JSON messages with machine telemetry
   - Publishes to MQTT topic: `cnc_machine/data`

2. **Message Enrichment** (As messages arrive)
   - `HydrationUtil` subscribes to MQTT topic
   - Retrieves context from Redis (`machine:energy:context`)
   - Merges context into message JSON
   - Publishes enriched message to Kafka topic: `cnc_data`

3. **Data Persistence** (Continuous polling)
   - `TimescaleUtil` consumes from Kafka topic: `cnc_data`
   - Parses JSON messages
   - Inserts data into TimescaleDB hypertable: `cnc_energy_events`
   - Handles three event types differently:
     - `ENERGY_SAMPLE`: Energy metrics
     - `MAINTENANCE_SAMPLE`: Maintenance metrics
     - `ANOMALY_DETECTED`: Anomaly data

4. **Data Aggregation** (Scheduled)
   - TimescaleDB continuous aggregates automatically refresh:
     - `hourly_energy_usage`: Every 3 minutes
     - `daily_maintenance_trend`: Every 5 minutes

5. **Visualization** (On-demand)
   - Grafana queries TimescaleDB
   - Displays real-time and historical metrics in dashboards

### Event Types and Frequencies

| Event Type | Frequency | Description |
|------------|-----------|-------------|
| ENERGY_SAMPLE | Every 15 seconds | Energy consumption, current, voltage, power factor |
| MAINTENANCE_SAMPLE | Every 60 seconds (4th iteration) | Maintenance due hours, tool usage, vibration, temperature |
| ANOMALY_DETECTED | Random (~25% chance) | Vibration spikes or power spikes |

---

## Message Formats

### MQTT Message Format (Before Enrichment)

#### ENERGY_SAMPLE Event

```json
{
  "machine": "CNC-ENERGY-01",
  "timestamp": 1234567890123,
  "event": "ENERGY_SAMPLE",
  "energyConsumptionKwh": 0.045,
  "currentDrawA": 15.5,
  "voltageV": 395.0,
  "powerFactor": 0.89
}
```

**Field Descriptions**:
- `machine`: Machine identifier (string)
- `timestamp`: Unix timestamp in milliseconds (long)
- `event`: Event type identifier (string)
- `energyConsumptionKwh`: Energy consumed in kilowatt-hours (double, 0.02-0.08)
- `currentDrawA`: Current draw in amperes (double, 8-23 A)
- `voltageV`: Voltage in volts (double, 380-420 V)
- `powerFactor`: Power factor (double, 0.8-0.98)

#### MAINTENANCE_SAMPLE Event

```json
{
  "machine": "CNC-ENERGY-01",
  "timestamp": 1234567890123,
  "event": "MAINTENANCE_SAMPLE",
  "maintenanceDueHours": 45.5,
  "toolUsageMinutes": 250.0,
  "spindleVibrationMm": 0.12,
  "bearingTemperature": 55.5
}
```

**Field Descriptions**:
- `maintenanceDueHours`: Hours until next maintenance (double, 10-90)
- `toolUsageMinutes`: Tool usage in minutes (double, 50-450)
- `spindleVibrationMm`: Spindle vibration in millimeters (double, 0.05-0.25)
- `bearingTemperature`: Bearing temperature in Celsius (double, 40-70)

#### ANOMALY_DETECTED Event

```json
{
  "machine": "CNC-ENERGY-01",
  "timestamp": 1234567890123,
  "event": "ANOMALY_DETECTED",
  "anomalyType": "VIBRATION_SPIKE",
  "anomalyValue": 0.45
}
```

**Anomaly Types**:
- `VIBRATION_SPIKE`: Spindle vibration anomaly (value: 0.30-0.60 mm)
- `POWER_SPIKE`: Power consumption anomaly (value: 1.1-1.5)

### Kafka Message Format (After Enrichment)

The message includes all original fields plus a `context` object:

```json
{
  "machine": "CNC-ENERGY-01",
  "timestamp": 1234567890123,
  "event": "ENERGY_SAMPLE",
  "energyConsumptionKwh": 0.045,
  "currentDrawA": 15.5,
  "voltageV": 395.0,
  "powerFactor": 0.89,
  "context": {
    "plant": "Linz Metal Works",
    "energyZone": "Grid-Segment-4",
    "maintenanceTeam": "Team Gamma",
    "maintenanceInterval": "Every 300 hours",
    "machineCategory": "EnergyOptimized"
  }
}
```

---

## Database Schema

### Main Table: `cnc_energy_events`

**Type**: Hypertable (TimescaleDB)

**Partitioning**: By `time` column (TIMESTAMPTZ)

**Primary Key**: (`time`, `machine`, `event_type`)

**Schema**:

```sql
CREATE TABLE cnc_energy_events (
    time TIMESTAMPTZ NOT NULL,
    machine TEXT NOT NULL,
    event_type TEXT NOT NULL CHECK (event_type IN (
        'ENERGY_SAMPLE',
        'MAINTENANCE_SAMPLE',
        'ANOMALY_DETECTED'
    )),
    -- Energy fields (populated for ENERGY_SAMPLE)
    energy_kwh DOUBLE PRECISION,
    current_a DOUBLE PRECISION,
    voltage_v DOUBLE PRECISION,
    power_factor DOUBLE PRECISION,
    -- Maintenance fields (populated for MAINTENANCE_SAMPLE)
    maintenance_due_hours DOUBLE PRECISION,
    tool_usage_minutes DOUBLE PRECISION,
    spindle_vibration_mm DOUBLE PRECISION,
    bearing_temp DOUBLE PRECISION,
    -- Anomaly fields (populated for ANOMALY_DETECTED)
    anomaly_type TEXT,
    anomaly_value DOUBLE PRECISION,
    -- Context fields (from Redis)
    plant TEXT,
    energy_zone TEXT,
    maintenance_team TEXT,
    maintenance_interval TEXT,
    machine_category TEXT,
    PRIMARY KEY (time, machine, event_type)
);
```

**Indexes**:
- Primary key index: (`time`, `machine`, `event_type`)
- Time index: `idx_energy_time_desc` on `time DESC`

**Retention Policy**: 90 days (automatically removes data older than 90 days)

### Continuous Aggregates

#### 1. `hourly_energy_usage`

**Purpose**: Hourly aggregated energy consumption metrics

**Refresh Policy**: Every 3 minutes, for data from 7 days ago to 3 minutes ago

```sql
CREATE MATERIALIZED VIEW hourly_energy_usage
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', time) AS bucket,
    machine,
    AVG(energy_kwh) AS avg_kwh,
    AVG(current_a) AS avg_current
FROM cnc_energy_events
WHERE event_type = 'ENERGY_SAMPLE'
GROUP BY bucket, machine;
```

**Aggregations**:
- `bucket`: Hour bucket (TIMESTAMPTZ)
- `machine`: Machine identifier
- `avg_kwh`: Average energy consumption in kWh
- `avg_current`: Average current draw in amperes

#### 2. `daily_maintenance_trend`

**Purpose**: Daily aggregated maintenance metrics

**Refresh Policy**: Every 5 minutes, for data from 60 days ago to 5 minutes ago

```sql
CREATE MATERIALIZED VIEW daily_maintenance_trend
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 day', time) AS bucket,
    machine,
    AVG(spindle_vibration_mm) AS avg_vibration,
    AVG(bearing_temp) AS avg_temperature
FROM cnc_energy_events
WHERE event_type = 'MAINTENANCE_SAMPLE'
GROUP BY bucket, machine;
```

**Aggregations**:
- `bucket`: Day bucket (TIMESTAMPTZ)
- `machine`: Machine identifier
- `avg_vibration`: Average spindle vibration in mm
- `avg_temperature`: Average bearing temperature in Celsius

### Query Examples

#### Recent Energy Samples

```sql
SELECT * FROM cnc_energy_events
WHERE event_type = 'ENERGY_SAMPLE'
ORDER BY time DESC
LIMIT 100;
```

#### Energy Consumption by Hour

```sql
SELECT * FROM hourly_energy_usage
WHERE bucket >= NOW() - INTERVAL '24 hours'
ORDER BY bucket DESC;
```

#### Anomalies in Last 24 Hours

```sql
SELECT time, machine, anomaly_type, anomaly_value
FROM cnc_energy_events
WHERE event_type = 'ANOMALY_DETECTED'
  AND time >= NOW() - INTERVAL '24 hours'
ORDER BY time DESC;
```

#### Maintenance Trends

```sql
SELECT * FROM daily_maintenance_trend
WHERE bucket >= NOW() - INTERVAL '30 days'
ORDER BY bucket DESC;
```

---

## Configuration

### Docker Compose Services

All services run in Docker containers orchestrated via `docker-compose.yml`.

**Network**: `advanced_data_management` (external network)

### Environment Variables

#### MqttOpcUaUtil
- `MQTT_BROKER`: MQTT broker URL (default: `tcp://mqtt_broker:1883`)

#### HydrationUtil
- `MQTT_BROKER`: MQTT broker URL (default: `tcp://mqtt_broker:1883`)
- `KAFKA_BROKER`: Kafka bootstrap servers (default: `redpanda_broker:9092`)
- `REDIS_HOST`: Redis hostname (default: `redis_container`)
- `REDIS_PORT`: Redis port (default: `6379`)

#### TimescaleUtil
- Hardcoded connection strings (modify source code to change)

### Configuration Files

#### `mqtt.conf`
MQTT broker configuration for Eclipse Mosquitto.

#### `redis.conf`
Redis server configuration.

#### `redis_hydration.yaml`
Redpanda Connect configuration for Redis hydration (alternative hydration path).

#### `pre_hydration.yaml`
Redpanda Connect configuration for pre-hydration (alternative path).

### Build Configuration

#### `pom.xml`
Maven project configuration with dependencies:
- Eclipse Paho MQTT Client 1.2.5
- Prosys OPC UA SDK (local JAR)
- SLF4J for logging
- BouncyCastle for cryptography
- Jedis for Redis
- Kafka Clients 3.9.1
- PostgreSQL JDBC Driver 42.7.3
- JSON.org 20240303

#### `Dockerfile`
Multi-stage build:
1. **Builder stage**: Maven build with Java 21
2. **Runtime stage**: OpenJDK 21 with built JARs

---

## Development Guide

### Project Structure

```
Advanced-Data-Management/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── prosysopc/
│       │           └── ua/
│       │               └── samples/
│       │                   ├── client/          # OPC UA client implementations
│       │                   ├── server/          # OPC UA server implementations
│       │                   │   ├── SampleConsoleServer.java
│       │                   │   ├── CncNodeManager.java
│       │                   │   └── ...
│       │                   └── util/            # Utility classes
│       │                       ├── MqttOpcUaUtil.java
│       │                       ├── HydrationUtil.java
│       │                       └── TimescaleUtil.java
│       ├── logbackClient.xml
│       └── logbackServer.xml
├── lib/                       # External JAR dependencies
│   ├── lib-mandatory/
│   ├── lib-opc-https/
│   ├── lib-pubsub-mqtt/
│   ├── lib-pubsub-samples/
│   ├── lib-samples/
│   └── prosys-opc-ua-sdk-evaluation-5.2.2-139.jar
├── target/                    # Build output
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── mqtt.conf
├── redis.conf
├── redis_hydration.yaml
└── pre_hydration.yaml
```

### Building the Project

#### Prerequisites
- Java 21 JDK
- Maven 3.6+

#### Build Command

```bash
mvn clean package
```

This creates four JAR files in `target/`:
- `MqttOpcUaUtil-jar-with-dependencies.jar`
- `SampleConsoleServer-jar-with-dependencies.jar`
- `TimescaleUtil-jar-with-dependencies.jar`
- `HydrationUtil-jar-with-dependencies.jar`

### Running Components Locally

#### Prerequisites
- Services (MQTT, Redis, Kafka, TimescaleDB) must be running and accessible

#### MqttOpcUaUtil

```bash
java --add-opens java.base/java.net=ALL-UNNAMED \
     -cp target/MqttOpcUaUtil-jar-with-dependencies.jar:lib/* \
     com.prosysopc.ua.samples.util.MqttOpcUaUtil
```

#### HydrationUtil

```bash
java --add-opens java.base/java.net=ALL-UNNAMED \
     -cp target/HydrationUtil-jar-with-dependencies.jar:lib/* \
     com.prosysopc.ua.samples.util.HydrationUtil
```

#### TimescaleUtil

```bash
java --add-opens java.base/java.net=ALL-UNNAMED \
     -cp target/TimescaleUtil-jar-with-dependencies.jar:lib/* \
     com.prosysopc.ua.samples.util.TimescaleUtil
```

#### SampleConsoleServer (OPC UA Server)

```bash
java --add-opens java.base/java.net=ALL-UNNAMED \
     -cp target/SampleConsoleServer-jar-with-dependencies.jar:lib/* \
     com.prosysopc.ua.samples.server.SampleConsoleServer
```

### Development Workflow

1. **Make code changes** in `src/main/java/`
2. **Rebuild** with `mvn clean package`
3. **Rebuild Docker images** (if needed):
   ```bash
   docker-compose build <service_name>
   ```
4. **Restart service**:
   ```bash
   docker-compose restart <service_name>
   ```

### Testing

#### Unit Testing
Currently no unit tests are configured. To add:
1. Add JUnit dependency to `pom.xml`
2. Create test classes in `src/test/java/`
3. Run with `mvn test`

#### Integration Testing
1. Start all services: `docker-compose up -d`
2. Monitor logs: `docker-compose logs -f <service>`
3. Verify data flow:
   - Check MQTT messages: Subscribe to `cnc_machine/data`
   - Check Kafka messages: Use Redpanda Console
   - Check database: Query TimescaleDB via pgAdmin

### Code Style

- Follow Java naming conventions
- Use meaningful variable names
- Add JavaDoc comments for public methods
- Handle exceptions appropriately

---

## Deployment Architecture

### Container Orchestration

All services are orchestrated via Docker Compose.

### Service Dependencies

```
redis_init (one-time)
    └── depends on: redis

opcua_stack
    └── depends on: mqtt

hydration_agent
    └── depends on: mqtt, redis, redpanda_broker

timescale_agent
    └── depends on: redpanda_broker, timescaledb

redpanda-console
    └── depends on: redpanda_broker

pgadmin
    └── depends on: timescaledb

grafana
    └── depends on: timescaledb
```

### Port Mappings

| Service | Internal Port | External Port | Protocol |
|---------|--------------|---------------|----------|
| MQTT | 1883 | 1883 | MQTT |
| MQTT Control Center | 8080 | 8080 | HTTP |
| MQTT SSL | 8883 | 8883 | MQTTS |
| Redis | 6379 | 6379 | Redis |
| RedisInsight | 8001 | 8001 | HTTP |
| Redpanda Kafka | 9092 | 19092 | Kafka |
| Redpanda Schema Registry | 8081 | 18081 | HTTP |
| Redpanda HTTP Proxy | 8082 | 18082 | HTTP |
| Redpanda Admin | 9644 | 19644 | HTTP |
| Redpanda Console | 8080 | 8087 | HTTP |
| TimescaleDB | 5432 | 5432 | PostgreSQL |
| Grafana | 3000 | 3000 | HTTP |
| pgAdmin | 80 | 5050 | HTTP |
| OPC UA Server | 52520 | 52599 | OPC UA |

### Volume Mounts

| Service | Volume | Purpose |
|---------|--------|---------|
| mqtt | `./eclipse-mosquitto-data` | MQTT broker data |
| mqtt | `./mqtt.conf` | MQTT configuration |
| redis | `./redis-data` | Redis persistence |
| redis | `./redis.conf` | Redis configuration |
| redpanda_broker | `./redpanda-data` | Redpanda data and logs |
| timescaledb | (Docker managed) | Database files |
| pgadmin | `./pgadmin-data` | pgAdmin sessions and data |
| hydration_agent | `./redis_hydration.yaml` | Redpanda Connect config |
| availability_hydration | `./pre_hydration.yaml` | Redpanda Connect config |

### Health Checks

**Redpanda** includes a health check:
```yaml
healthcheck:
  test: ["CMD-SHELL", "rpk cluster health | grep -E 'Healthy:.+true' || exit 1"]
  interval: 15s
  timeout: 3s
  retries: 5
  start_period: 5s
```

Other services use restart policies (`restart: unless-stopped` or `restart: always`).

### Scaling Considerations

- **MQTT Broker**: Can be scaled horizontally with MQTT clustering (not configured)
- **Kafka/Redpanda**: Can be scaled to multi-broker cluster (currently single broker)
- **TimescaleDB**: Can use read replicas for query scaling (currently single instance)
- **Agents** (MqttOpcUaUtil, HydrationUtil, TimescaleUtil): Stateless, can be scaled horizontally

### Resource Requirements

**Minimum**:
- CPU: 4 cores
- RAM: 8 GB
- Disk: 20 GB

**Recommended**:
- CPU: 8 cores
- RAM: 16 GB
- Disk: 100 GB (for time-series data)

---

## Protocols and APIs

### OPC UA

**Protocol**: OPC Unified Architecture

**Server Endpoint**: `opc.tcp://localhost:52599`

**Namespace**: `http://example.com/CNC`

**Address Space**: Managed by `CncNodeManager`

**Key Nodes**:
- Machine status
- Spindle speeds (target/actual)
- Feed rates (target/actual)
- Tool life
- Position coordinates (X, Y, Z)
- Production metrics
- Alarm conditions

### MQTT

**Protocol**: MQTT 3.1.1

**Broker**: `tcp://localhost:1883` (or `tcp://mqtt_broker:1883` in containers)

**Topics**:
- `cnc_machine/data`: Primary data topic

**QoS Level**: 1 (At least once delivery)

**Message Format**: JSON

**Client Libraries**: Eclipse Paho MQTT Client 1.2.5

### Kafka/Redpanda

**Protocol**: Kafka Protocol (via Redpanda)

**Bootstrap Servers**: 
- External: `localhost:19092`
- Internal: `redpanda_broker:9092`

**Topics**:
- `cnc_data`: Main data pipeline
- `usecase_hydration`: Alternative hydration path

**Consumer Group**: `timescale-agent-group-v3`

**Serialization**: String (JSON)

**Client Libraries**: Apache Kafka Clients 3.9.1

### Redis

**Protocol**: Redis Protocol

**Endpoint**: `localhost:6379` (or `redis_container:6379` in containers)

**Data Structure**: String (JSON stored as string)

**Key**: `machine:energy:context`

**Client Library**: Jedis 5.1.0

### PostgreSQL/TimescaleDB

**Protocol**: PostgreSQL Protocol

**Connection String**: `jdbc:postgresql://timescaledb:5432/mydb`

**JDBC Driver**: PostgreSQL JDBC Driver 42.7.3

**TimescaleDB Extension**: Enabled for hypertable functionality

---

## Additional Notes

### Java Version

The project requires **Java 21** due to:
- Text blocks (triple-quoted strings)
- Pattern matching enhancements
- Record classes (if used)

### Prosys OPC UA SDK License

This project uses the **Prosys OPC UA SDK Evaluation version**. For production use, ensure you have appropriate licensing from Prosys OPC.

### Data Retention

- **TimescaleDB**: 90 days automatic retention
- **Kafka/Redpanda**: Default retention (configurable)
- **Redis**: Persistent (via AOF and RDB snapshots)
- **MQTT**: No persistence by default

### Security Considerations

**Current Configuration** (Development/Evaluation):
- Default passwords used throughout
- No SSL/TLS encryption (except MQTT SSL port available)
- No authentication required for most services

**Production Recommendations**:
- Change all default passwords
- Enable SSL/TLS for all connections
- Implement proper authentication and authorization
- Use secrets management (e.g., Docker Secrets, Vault)
- Enable network segmentation
- Regular security updates

### Monitoring and Observability

- **Grafana**: Metrics visualization
- **pgAdmin**: Database administration
- **Redpanda Console**: Kafka topic monitoring
- **RedisInsight**: Redis monitoring
- **MQTT Control Center**: MQTT broker monitoring
- **Docker logs**: Application logs via `docker-compose logs`

### Performance Tuning

**TimescaleDB**:
- Adjust `chunk_time_interval` for hypertables based on data volume
- Tune continuous aggregate refresh policies
- Consider compression policies for older data

**Kafka/Redpanda**:
- Adjust topic partition count for parallel processing
- Tune consumer batch sizes
- Configure appropriate retention policies

**Redis**:
- Configure memory limits
- Tune persistence (AOF vs RDB)
- Consider Redis Cluster for high availability

---

## References

- [Prosys OPC UA SDK Documentation](https://www.prosysopc.com/products/opc-ua-java-sdk/)
- [TimescaleDB Documentation](https://docs.timescale.com/)
- [Redpanda Documentation](https://docs.redpanda.com/)
- [Eclipse Mosquitto Documentation](https://mosquitto.org/documentation/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Redis Documentation](https://redis.io/docs/)


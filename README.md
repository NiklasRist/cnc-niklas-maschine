## Prerequisites

Before running the system, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.6+** for building the Java applications
- **Docker** and **Docker Compose** for running containerized services
- **Git** (optional, for cloning the repository)

## Quick Start

### 1. Create Docker Network

The system requires a Docker network named `advanced_data_management`. Create it if it doesn't exist:

```bash
docker network create advanced_data_management
```

### 2. Build the Java Applications

Build the Maven project to generate the required JAR files:

```bash
mvn clean package
```

This will create the following JAR files in the `target/` directory:
- `MqttOpcUaUtil-jar-with-dependencies.jar`
- `SampleConsoleServer-jar-with-dependencies.jar`
- `TimescaleUtil-jar-with-dependencies.jar`
- `HydrationUtil-jar-with-dependencies.jar`

### 3. Start All Services

Start all services using Docker Compose:

```bash
docker-compose up -d
```

This will start the following services:
- **MQTT Broker** (Eclipse Mosquitto) on ports 1883, 8080, 8883
- **Redis** on port 6379 (RedisInsight on port 8001)
- **Redpanda** (Kafka broker) on port 19092
- **Redpanda Console** on port 8087
- **TimescaleDB** on port 5432
- **Grafana** on port 3000
- **pgAdmin** on port 5050
- **OPC UA Stack** (server + MQTT agent)
- **Hydration Agent** (enriches MQTT data with Redis context)
- **Timescale Agent** (consumes Kafka and writes to TimescaleDB)

### 4. Verify Services

Check that all containers are running:

```bash
docker-compose ps
```

All services should show as "Up" or "running".

## Service Access Information

### Web Interfaces

- **Grafana**: http://localhost:3000
  - Username: `admin`
  - Password: `passwort123`

- **pgAdmin**: http://localhost:5050
  - Email: `admin@example.com`
  - Password: `passwort123`

- **Redpanda Console**: http://localhost:8087

- **RedisInsight**: http://localhost:8001

- **MQTT Control Center**: http://localhost:8080

### Database Connections

- **TimescaleDB**:
  - Host: `timescaledb`
  - Port: `5432`
  - Database: `mydb`
  - Username: `admin`
  - Password: `passwort123`

- **Redis**:
  - Host: `localhost`
  - Port: `6379`

### MQTT

- **MQTT Broker**: `tcp://localhost:1883`
- **MQTT Topic**: `cnc_machine/data`

### Kafka/Redpanda

- **Bootstrap Servers**: `localhost:19092`
- **Topics**: `cnc_data`, `usecase_hydration`

### OPC UA

- **OPC UA Server**: `opc.tcp://localhost:52599`

## Configuration Files

- `mqtt.conf` - MQTT broker configuration
- `redis.conf` - Redis server configuration

## Stopping the System

To stop all services:

```bash
docker-compose down
```

To stop and remove volumes (this will delete all data):

```bash
docker-compose down -v
```

## Troubleshooting

### Check Service Logs

View logs for a specific service:

```bash
docker-compose logs <service_name>
```

For example:
```bash
docker-compose logs opcua_stack
docker-compose logs hydration_agent
docker-compose logs timescale_agent
```

### View All Logs

```bash
docker-compose logs -f
```

### Check Container Status

```bash
docker-compose ps
```

### Verify Network

Ensure the Docker network exists:

```bash
docker network ls | grep advanced_data_management
```

If missing, create it:
```bash
docker network create advanced_data_management
```


### Database Connection Issues

If TimescaleDB is not accessible:
1. Check if the container is running: `docker-compose ps timescaledb`
2. Check logs: `docker-compose logs timescaledb`
3. Verify port 5432 is not already in use



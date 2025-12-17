# --- 1. Build-Stage: JAR-Datei erzeugen ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Arbeitsverzeichnis
WORKDIR /build

# pom.xml und src kopieren
COPY pom.xml ./
COPY src ./src
COPY lib ./lib

# Maven-Build ausführen
RUN mvn clean package -DskipTests

# --- 2. Laufzeit-Stage ---
FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Beide JARs aus der Build-Stage kopieren
COPY --from=builder /build/target/MqttOpcUaUtil*-jar-with-dependencies.jar ./MqttOpcUaUtil-jar-with-dependencies.jar
COPY --from=builder /build/target/SampleConsoleServer*-jar-with-dependencies.jar ./SampleConsoleServer-jar-with-dependencies.jar
COPY --from=builder /build/target/TimescaleUtil*-jar-with-dependencies.jar ./TimescaleUtil-jar-with-dependencies.jar
COPY --from=builder /build/target/HydrationUtil*-jar-with-dependencies.jar ./HydrationUtil-jar-with-dependencies.jar

COPY lib ./lib

EXPOSE 52520


CMD bash -c "\
  echo 'Starting SampleConsoleServer...' && \
  java --add-opens java.base/java.net=ALL-UNNAMED \
       -cp SampleConsoleServer-jar-with-dependencies.jar:lib/* \
       com.prosysopc.ua.samples.server.SampleConsoleServer & \
  echo 'Waiting for OPC UA Server to be ready on port 52520...' && \
  while ! nc -z localhost 52520; do \
    echo '⚙Server not ready yet, waiting 3s...'; \
    sleep 3; \
  done; \
  echo 'OPC UA Server is ready!' && \
  echo 'Starting MqttOpcUaUtil...' && \
  java --add-opens java.base/java.net=ALL-UNNAMED \
       -cp MqttOpcUaUtil-jar-with-dependencies.jar:lib/* \
       com.prosysopc.ua.samples.util.MqttOpcUaUtil"

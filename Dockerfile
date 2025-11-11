# --- STAGE 1: build with Maven ---
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace
# Copy pom + source
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
# jar build
RUN mvn -q -DskipTests package

# --- STAGE 2: runtime ---
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# Copy only the .jar
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

# JVM flags para limitar o uso percebido de CPU/RAM pela JVM
# -XX:ActiveProcessorCount=1 força a JVM a considerar 1 CPU
# -Xms128m, -Xmx700m limitam heap (reserve espaço para overhead do container)
# Ajuste -Xmx conforme sua necessidade (deixe margem para metaspace, threads, native)
ENV JAVA_OPTS="-XX:ActiveProcessorCount=1 -Xms128m -Xmx400m -XX:MaxRamPercentage=85.0"

ENTRYPOINT [ "sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar" ]

FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \mvn dependency:go-offline -B

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache tzdata \
    && addgroup -S appgroup \
    && adduser -S appuser -G appgroup

ENV TZ=America/Sao_Paulo
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Duser.timezone=America/Sao_Paulo"

WORKDIR /app

COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
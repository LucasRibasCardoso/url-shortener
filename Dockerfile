FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl tzdata

ENV TZ=America/Sao_Paulo
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser
EXPOSE 8080

ENV OTEL_SDK_DISABLED=true
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "unset JAVA_TOOL_OPTIONS; java $JAVA_OPTS -Duser.timezone=America/Sao_Paulo -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

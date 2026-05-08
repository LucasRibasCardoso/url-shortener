FROM eclipse-temurin:21-jre-alpine AS builder

WORKDIR /builder

COPY target/*.jar app.jar

RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache tzdata \
    && addgroup -S appgroup \
    && adduser -S appuser -G appgroup

ENV TZ=America/Sao_Paulo
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Duser.timezone=America/Sao_Paulo"

WORKDIR /app

COPY --from=builder --chown=appuser:appgroup /builder/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /builder/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /builder/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /builder/application/ ./

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
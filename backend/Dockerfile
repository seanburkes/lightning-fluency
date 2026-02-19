FROM docker.io/library/gradle:8.12-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY src ./src
RUN gradle buildFatJar --no-daemon

FROM docker.io/library/eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -g 1000 -S appgroup && \
    adduser -S -D -H -u 1000 -G appgroup appuser

RUN mkdir -p /data /backups && chown -R appuser:appgroup /data /backups

COPY --from=build /app/build/libs/lute-backend.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser
ENV LUTE_DB_PATH=/data/lute.db
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

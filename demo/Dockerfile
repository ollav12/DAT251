FROM gradle:jdk21 AS builder

WORKDIR /app
COPY . .

RUN gradle build --no-daemon

FROM openjdk:21-jdk-slim AS runner

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=nodb

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

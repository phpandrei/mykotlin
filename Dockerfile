# Этап сборки
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle installDist

# Финальный образ
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/install/kotlin-first .
EXPOSE 8080
CMD ["./bin/kotlin-first"]

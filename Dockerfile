#Stage 1: Build the app
FROM gradle:8.4-jdk17 AS builder
WORKDIR /build
COPY . .
RUN gradle bootJar --no-daemon

#Stage 2: Run the app
FROM eclipse-temurin:17-jre
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar
RUN chown -R spring:spring /app
USER spring
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

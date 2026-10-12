FROM eclipse-temurin:25-jdk-alpine-3.23 AS build
WORKDIR /build

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --no-daemon --version

COPY src src
RUN ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:25-jre-alpine-3.23
RUN addgroup -S app && adduser -S -G app app
WORKDIR /app
COPY --from=build /build/build/libs/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]

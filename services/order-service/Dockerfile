FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
# Dependencies first, so source changes do not re-download them.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# The tests run on their own, outside the image build; this stage only packages.
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:17-jre-noble
WORKDIR /app
COPY --from=build /build/target/order-service.jar app.jar
RUN useradd --system --no-create-home order
USER order
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

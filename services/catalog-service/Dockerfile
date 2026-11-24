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
COPY --from=build /build/target/catalog-service.jar app.jar
# Fixed ids: the agnello-pki volume gives group 10000 (agnello) read access to the keys and secrets.
RUN groupadd --system --gid 10000 agnello && useradd --system --no-create-home --uid 10001 --gid 10000 catalog
USER catalog
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

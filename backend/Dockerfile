FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy maven files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dodaj uprawnienia wykonywania dla mvnw
RUN chmod +x ./mvnw

# Copy source code
COPY src src

# Build the application (Maven downloads only required deps, not the full BOM)
RUN ./mvnw package -DskipTests

# Run the application
ENTRYPOINT ["java", "-jar", "target/eldorado-0.0.1-SNAPSHOT.jar"]
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy maven files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Run the application
ENTRYPOINT ["java", "-jar", "target/eldorado-0.0.1-SNAPSHOT.jar"]
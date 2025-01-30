# !------------- WARNING -------------!
#
# This file isn't functioning in this build.
# It's a merely informative file as to how to
# Deploy this app. You MUST include your own
# application.properties for this to work!
#
# -------------------------------------

FROM eclipse-temurin:23-jdk AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven build file and the source code
COPY pom.xml .
COPY src ./src

# Copy the Maven wrapper files
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Ensure mvnw has execute permissions
RUN chmod +x mvnw

# Run the Maven build
RUN ./mvnw dependency:go-offline

# Package the application
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:23-jre
# Copy the built jar file to the container
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
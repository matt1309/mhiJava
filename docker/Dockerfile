FROM eclipse-temurin:21-jre

# Install jq for JSON processing in the entrypoint script
RUN apt-get update && apt-get install -y --no-install-recommends jq && \
    rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy the entrypoint script and make it executable
COPY docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Copy the pre-built JAR
COPY mhiJava.jar /app/mhiJava.jar

# Use the entrypoint script to generate config.json from environment variables
ENTRYPOINT ["docker-entrypoint.sh"]

# Default command - run the Java application
CMD ["java", "-jar", "/app/mhiJava.jar"]
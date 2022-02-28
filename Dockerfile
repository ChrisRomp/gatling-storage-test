FROM mcr.microsoft.com/vscode/devcontainers/java:0-8-bullseye
WORKDIR /app
COPY ./storage-test ./

# Simulation Files
VOLUME /app/src/gatling/java/storageSimulation

# Resource Files including fileList.csv
VOLUME /app/src/gatling/resources

# Gatling results
VOLUME /app/build/reports

# Do an initial build when creating the container just to ensure
# everything is working.
RUN ["./gradlew", "build", "--no-daemon"]

# Default run - can override from docker run
CMD ["./gradlew", "--no-daemon", "gatlingRun"]

# Gatling Storage Download Test

## Configuring the Simulations
Place simulation class files in the `/simulations` folder. You can use the `/template/TemplateSimulation.java` file as a template.

Follow the `[Name]Simulation` class naming convention, and likewise the `[Name]Simulation.java` filename convention.

### Download Files
The default file list is in `/resources/fileList.csv`. You can configure alternate lists by modifying this file, or changing the `fileListName` variable in the simulation class file(s).

## Running the Simulations with Docker
To build the Docker container, execute:
```bash
./docker-build.sh
```

To run the simulations container, execute:
```bash
./docker-run-test.sh
```

You can modify `JAVA_OPTS` or other parameters by modifying `docker-run-test.sh`.

## Viewing the Results
The results are output to the `reports/gatling` folder under a folder for each simulation. Details of the report structure can be found in the Gatling documentation: https://gatling.io/docs/gatling/reference/current/stats/reports/

## Development
This repository contains files for [VS Code Remote Containers](https://code.visualstudio.com/docs/remote/containers) development in the `.devcontainer` folder. The container is based on Java 8 Debian Bullseye (11.2).

---

_Based on the Gatling Gradle sample at_  
_https://github.com/gatling/gatling-gradle-plugin-demo-java_

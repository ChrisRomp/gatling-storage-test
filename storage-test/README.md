# Gatling Storage Download Test

Based on the Gatling Gradle sample at: 
https://github.com/gatling/gatling-gradle-plugin-demo-java

Gatling Gradle Plugin Documentation: https://gatling.io/docs/gatling/reference/current/extensions/gradle_plugin/

## Running the Simulations
To run all simulations with default simulation parameters:
```bash
cd storage-test
./gradlew gatlingRun
```

### Running a Specific Simulation
To run only a single simulation, you can pass the simulation class name appended to the `gatlingRun` command with a dash `-SimulationClassName`, e.g.:
```bash
./gradlew gatlingRun-storageSimulation.AzureEastUs2Simulation
```

### Optional Parameters

| Parameter | Type | Default | Description |
| --- | --- | --- | --- |
| iterationCount | integer | 200 | Override the number of test iterations |

Parameters can be set using the `JAVA_OPTS` environment variable or on the command line ([more info](https://gatling.io/docs/gatling/guides/passing_parameters/)).

### Using `JAVA_OPTS`
```bash
cd stroage-test
JAVA_OPTS="-DiterationCount=100"
./gradlew gatlingRun
```

### Using the Command Line
```bash
gradlew gatlingRun -DiterationCount=100
```

## File List
The file list is a .csv file located in `src/gatling/resources/fileList.csv`.

## Modifying Scenarios
Scenarios can be added or modified by editing the `.java` files within the `src/gatling/java/storageSimulation` folder.

## Viewing the Results
The results are output to the `build/reports/gatling` folder under a folder for each simulation. Details of the report structure can be found in the Gatling documentation: https://gatling.io/docs/gatling/reference/current/stats/reports/

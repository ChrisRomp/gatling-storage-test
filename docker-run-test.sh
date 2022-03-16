docker run --name gatling-storage --rm -it \
  -v $(pwd)/simulations:/app/src/gatling/java/storageSimulation \
  -v $(pwd)/resources:/app/src/gatling/resources \
  -v $(pwd)/reports:/app/reports \
  -v $(pwd)/files:/app/files \
  --env-file ./test-vars.env \
  gatling-storage-test $1

# OPTIONAL - can override simulation run count globally adding JAVA_OPTS to the test-vars.env file, e.g.:
#  JAVA_OPTS=-DiterationCount=200 \

# OPTIONAL - you can pass an argument, e.g., bash to drop into the shell
# ./docker-run-test.sh bash

# To run this completely in the background, change -it to -d in the first line and 
# monitor the progress with via docker logs

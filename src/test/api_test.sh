#!/bin/bash

export WORKING_DIR='api_test'
echo "> Working dir: $WORKING_DIR"
mkdir ./$WORKING_DIR

echo "> Getting data..."
git clone https://github.com/gothinkster/realworld.git ./$WORKING_DIR

echo "> Start DB"
nohup docker-compose up -d > /dev/null &

echo "> Start API"
nohup sbt run start &> /dev/null &

echo "> Start Test"
#APIURL=localhost:9000/api ./$WORKING_DIR/api/run-api-tests.sh

# Clean after test
docker-compose down


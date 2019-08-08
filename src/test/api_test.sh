#!/bin/bash

export WORKING_DIR='api_test'
echo "> Working dir: $WORKING_DIR"
mkdir ./$WORKING_DIR

echo "> Getting data..."
git clone https://github.com/gothinkster/realworld.git ./$WORKING_DIR

echo "> Start Application "
docker-compose up -d

sleep 5

echo "> Start Test"
APIURL=localhost:9000/api ./$WORKING_DIR/api/run-api-tests.sh

# Clean after test
docker-compose down


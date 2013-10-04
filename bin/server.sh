#!/bin/bash

# config
PORT="8080"

# constants
VERSION="0.1.0"
SERVER_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../server"
JAR="$SERVER_DIR/target/tunebot-server-$VERSION-jar-with-dependencies.jar"

# create the jar if it does not exist
if [ ! -f $JAR ]
then
  cd $SERVER_DIR
  mvn package
fi

# run the server
export PORT
java -jar $JAR

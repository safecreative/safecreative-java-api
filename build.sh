#!/bin/bash
cwd=$(pwd)
#Build all modules
mvn clean
mvn install -DskipTests=true 
#Build assembly java api
cd safecreative-java-api
mvn assembly:assembly -DskipTests=true 
cd $cwd

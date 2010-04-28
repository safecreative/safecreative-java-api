#!/bin/bash
cwd=$(pwd)
cd safecreative-java-api
mvn assembly:assembly -DskipTests=true 
cd $cwd/safecreative-java-api-examples
mvn install



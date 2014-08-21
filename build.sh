#!/bin/bash
cwd=$(pwd)
#Build all modules
mvn clean
mvn install -DskipTests=true 
#Build assembly java api
cd safecreative-java-api
mvn assembly:assembly -DskipTests=true 
mvn javadoc:javadoc -DskipTests=true
cp -r target/*.jar target/site/apidocs LICENSE.txt README target/archive-tmp
cd target/archive-tmp
zip -r ../safecreative-java-api.zip *
rm -r *
cd $cwd


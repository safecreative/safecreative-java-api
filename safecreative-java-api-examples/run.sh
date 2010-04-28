#!/bin/bash
#API Keys:
sharedKey=<YOUR SHARED KEY>
privateKey=<YOUR PRIVATE KEY>

version=0.5.0
apiLib=../safecreative-java-api/target/safecreative-java-api-$version-SNAPSHOT-jar-with-dependencies.jar
examplesLib=target/safecreative-java-api-examples-$version.jar

class=$1
shift 1
java -classpath $apiLib:$examplesLib org.safecreative.api.examples.$class $sharedKey $privateKey $@






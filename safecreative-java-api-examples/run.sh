#!/bin/bash
#API Keys:
sharedKey=7gi3unfhknsdy0gjaogsa7qbj
privateKey=1drq9xl7y08p852pc9zxdw5ob

version=0.5.0
apiLib=../safecreative-java-api/target/safecreative-java-api-$version-SNAPSHOT-jar-with-dependencies.jar
examplesLib=target/safecreative-java-api-examples-$version.jar

class=$1
shift 1
java -classpath $apiLib:$examplesLib org.safecreative.api.examples.$class $sharedKey $privateKey $@






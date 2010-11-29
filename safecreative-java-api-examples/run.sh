#!/bin/bash
#API Keys:
sharedKey=504kre873efbl37jjf9oxq3i3
privateKey=763o0yxvq44ek25fzcllepnv9

api_version=0.6.5
examples_version=0.5.0
apiLib=../safecreative-java-api/target/safecreative-java-api-$api_version-jar-with-dependencies.jar
examplesLib=target/safecreative-java-api-examples-$examples_version.jar

class=$1
shift 1
java -Dsc.api.debug=DEBUG -Dsc.api.root.debug=ERROR -classpath $apiLib:$examplesLib org.safecreative.api.examples.$class $sharedKey $privateKey $@






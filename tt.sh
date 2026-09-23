#!/usr/bin/env bash
set -euox pipefail
#./gradlew build
java -cp build/libs/Triangle-Tools.jar triangle.Compiler "$1" -o=obj.tam
java -cp build/libs/Triangle-Tools.jar triangle.abstractMachine.Interpreter obj.tam
#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
rm -rf bin && mkdir -p bin
javac -cp lib/sqlite-jdbc.jar -d bin src/*.java
java -cp bin:lib/sqlite-jdbc.jar Main

#!/bin/bash

mkdir -p build
rm -rf build/*

javac -source 1.8 -target 1.8 -cp .:../../guice/lib/javax.inject.jar:../../guice/core/target/guice-4.2.4-SNAPSHOT.jar Harness.java -d build

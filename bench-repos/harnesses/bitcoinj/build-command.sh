#!/bin/bash

mkdir -p build/
rm -rf build/*

javac -source 1.8 -target 1.8 -cp  ../../bitcoinj/core/build/libs/bitcoinj-core-0.16-SNAPSHOT.jar:. Harness.java -d build/

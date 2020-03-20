#!/bin/bash

mkdir -p build/json2/
rm -rf build/json2/*

javac -source 1.8 -target 1.8 -cp ../../Hystrix/hystrix-core/build/libs/hystrix-core-0.1.0-SNAPSHOT.jar:.:../../Hystrix/hystrix-serialization/build/libs/hystrix-serialization-0.1.0-SNAPSHOT.jar JsonHarness2.java -d build/json2

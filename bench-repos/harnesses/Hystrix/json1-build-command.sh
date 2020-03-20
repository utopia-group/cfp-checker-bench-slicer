#!/bin/bash

mkdir -p build/json1/
rm -rf build/json1/*

javac -source 1.8 -target 1.8 -cp ../../Hystrix/hystrix-core/build/libs/hystrix-core-0.1.0-SNAPSHOT.jar:.:../../Hystrix/hystrix-serialization/build/libs/hystrix-serialization-0.1.0-SNAPSHOT.jar JsonHarness1.java -d build/json1

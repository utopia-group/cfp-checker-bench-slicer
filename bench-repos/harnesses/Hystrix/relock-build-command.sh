#!/bin/bash

mkdir -p build/relock/
rm -rf build/relock/*

javac -source 1.8 -target 1.8 -cp ../../Hystrix/hystrix-core/build/libs/hystrix-core-0.1.0-SNAPSHOT.jar:. ReentrantLockHarness.java -d build/relock

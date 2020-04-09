#!/bin/bash

mkdir -p build
rm -rf build/*

javac -source 1.8 -target 1.8 -cp ../../deps/android.jar:.:jars/glide-4.11.0.jar Harness.java -d build

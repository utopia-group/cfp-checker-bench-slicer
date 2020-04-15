#!/bin/bash

mkdir -p build
rm -rf build/*

javac -source 1.8 -target 1.8 -cp ../../litho/litho-core/build/intermediates/compile_library_classes/release/classes.jar:.:../../deps/android.jar Harness.java -d build

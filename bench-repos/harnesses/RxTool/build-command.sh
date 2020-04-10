#!/bin/bash

mkdir -p build
rm -rf build/*

javac -source 1.8 -target 1.8 -cp ../../deps/android.jar:.:../../RxTool/RxUI/build/intermediates/compile_library_classes/release/classes.jar Harness.java -d build

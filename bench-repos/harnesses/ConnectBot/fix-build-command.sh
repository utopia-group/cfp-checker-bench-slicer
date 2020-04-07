#!/bin/bash

mkdir -p build/fix/
rm -rf build/fix/*

javac -source 1.8 -target 1.8 -cp ../../deps/android.jar:.:jars/ConnectBot-svn-r501-english-dex2jar.jar android/app/ActivityHarness.java -d build/fix/

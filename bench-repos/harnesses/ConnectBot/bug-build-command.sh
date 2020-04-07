#!/bin/bash

mkdir -p build/bug/
rm -rf build/bug/*

javac -source 1.8 -target 1.8 -cp ../../deps/android.jar:.:jars/ConnectBot-svn-r496-english-dex2jar.jar android/app/ActivityHarness.java -d build/bug/

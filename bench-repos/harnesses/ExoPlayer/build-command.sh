#!/bin/bash

mkdir -p $1
rm -rf $1/*

javac -source 1.8 -target 1.8 -cp ../../deps/junit-4.13.jar:../../deps/android.jar:../../ExoPlayer/library/core/src/main/java:../../deps/annotation-1.1.0.jar:../../deps/checker-qual.jar:../../deps/checker-compat-qual-2.5.5.jar:../../ExoPlayer/testutils/src/main/java/ -d $1 android/app/ActivityHarness.java

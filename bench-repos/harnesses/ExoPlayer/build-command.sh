#!/bin/bash

javac -cp ../../deps/junit-4.13.jar:../../deps/android.jar:../../ExoPlayer/library/core/src/main/java:../../deps/annotation-1.1.0.jar:../../deps/checker-qual.jar:../../deps/checker-compat-qual-2.5.5.jar:../../ExoPlayer/testutils/src/main/java/ -d build/ android/app/ActivityHarness.java

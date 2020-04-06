#!/bin/bash

mkdir -p build/
rm -rf build/*

javac -source 1.8 -target 1.8 -cp  ../../deps/jackson-core-2.7.5.jar:../../hadoop/hadoop-common-project/hadoop-common/target/hadoop-common-3.3.0-SNAPSHOT.jar:../../hadoop/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-core/target/hadoop-mapreduce-client-core-3.3.0-SNAPSHOT.jar:. org/apache/hadoop/mapred/Harness.java -d build/

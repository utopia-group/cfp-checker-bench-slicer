#!/bin/bash

JAR="target/cfpchecker-bench-slicer-1.0-SNAPSHOT-jar-with-dependencies.jar"
SOOT_COMMON_OPTS="-keep-line-number -w -exclude java* -no-bodies-for-excluded -allow-phantom-refs"

# ConnectBot-Bug
echo "Slicing ConnectBot-Bug"
rm -rf ConnectBot-Bug
(cd bench-repos/connectbot/; git checkout b8ec5e60fd3231c1fe96c3fc7dbd9cc2e640ce4d)
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/ConnectBot/bug-wala.input.txt 'Landroid/os/PowerManager$WakeLock' acquire release --  -process-dir bench-repos/harnesses/ConnectBot/build/bug -process-dir bench-repos/harnesses/ConnectBot/jars/ConnectBot-svn-r496-english-dex2jar.jar -output-dir ConnectBot-Bug ${SOOT_COMMON_OPTS}

# ConnectBot-Fix
echo "Slicing ConnectBot-Fix"
rm -rf ConnectBot-Fix
(cd bench-repos/connectbot/; git checkout f5d392e3a334d230fb782a1faff37bda8153750f)
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/ConnectBot/fix-wala.input.txt 'Landroid/os/PowerManager$WakeLock' acquire release --  -process-dir bench-repos/harnesses/ConnectBot/build/fix -process-dir bench-repos/harnesses/ConnectBot/jars/ConnectBot-svn-r501-english-dex2jar.jar -output-dir ConnectBot-Fix ${SOOT_COMMON_OPTS}

# ExoPlayer-Bug
echo "Slicing ExoPlayer-Bug (WakeLock)"
rm -rf ExoPlayer-WakeLock-Bug
(cd bench-repos/ExoPlayer/; git checkout -- testutils/src/main/java/com/google/android/exoplayer2/testutil/HostActivity.java)
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/ExoPlayer/bug-wala.input.txt 'Landroid/os/PowerManager$WakeLock' acquire release -- -process-dir bench-repos/harnesses/ExoPlayer/exoplayer-bug/ -output-dir ExoPlayer-WakeLock-Bug ${SOOT_COMMON_OPTS}

echo "Slicing ExoPlayer-Bug (WifiLock)"
rm -rf ExoPlayer-WifiLock-Bug
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/ExoPlayer/bug-wala.input.txt 'Landroid/net/wifi/WifiManager$WifiLock' acquire release -- -process-dir bench-repos/harnesses/ExoPlayer/exoplayer-bug/ -output-dir ExoPlayer-WifiLock-Bug ${SOOT_COMMON_OPTS}

# ExoPlayer-Fix
echo "Slicing ExoPlayer-Fix (WakeLock)"
rm -rf ExoPlayer-WakeLock-Fix
(cd bench-repos/ExoPlayer; git apply ../harnesses/ExoPlayer/hostactivity-fix.patch)
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/ExoPlayer/fix-wala.input.txt 'Landroid/os/PowerManager$WakeLock' acquire release -- -process-dir bench-repos/harnesses/ExoPlayer/exoplayer-fix/ -output-dir ExoPlayer-WakeLock-Fix ${SOOT_COMMON_OPTS}

echo "Slicing ExoPlayer-Fix (WifiLock)"
rm -rf ExoPlayer-WifiLock-Fix
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/ExoPlayer/fix-wala.input.txt 'Landroid/net/wifi/WifiManager$WifiLock' acquire release -- -process-dir bench-repos/harnesses/ExoPlayer/exoplayer-fix/ -output-dir ExoPlayer-WifiLock-Fix ${SOOT_COMMON_OPTS}

# Hystrix Relock
echo "Slicing Hystrix-Relock"
rm -rf Hystrix-ReLock
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/Hystrix/relock-wala.input.txt Ljava/util/concurrent/locks/ReentrantLock lock unlock tryLock -- -process-dir bench-repos/harnesses/Hystrix/build/relock/ -process-dir bench-repos/Hystrix/hystrix-core/build/libs/hystrix-core-0.1.0-SNAPSHOT.jar -output-dir Hystrix-ReLock ${SOOT_COMMON_OPTS}

# Hystrix Json1
echo "Slicing Hystrix-Json1"
rm -rf Hystrix-Json1
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/Hystrix/json1-wala.inupt.txt Lcom/fasterxml/jackson/core/JsonGenerator '*' -- -process-dir bench-repos/harnesses/Hystrix/build/json1 -process-dir bench-repos/Hystrix/hystrix-core/build/libs/hystrix-core-0.1.0-SNAPSHOT.jar -process-dir bench-repos/Hystrix/hystrix-serialization/build/libs/hystrix-serialization-0.1.0-SNAPSHOT.jar -output-dir Hystrix-Json1 ${SOOT_COMMON_OPTS}

# Hystrix Json2
echo "Slicing Hystrix-Json2"
rm -rf Hystrix-Json2
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/Hystrix/json2-wala.inupt.txt Lcom/fasterxml/jackson/core/JsonGenerator '*' -- -process-dir bench-repos/harnesses/Hystrix/build/json2 -process-dir bench-repos/Hystrix/hystrix-core/build/libs/hystrix-core-0.1.0-SNAPSHOT.jar -process-dir bench-repos/Hystrix/hystrix-serialization/build/libs/hystrix-serialization-0.1.0-SNAPSHOT.jar -output-dir Hystrix-Json2 ${SOOT_COMMON_OPTS}

# bitcoinj
echo "Slicing bitcoinj"
rm -rf Bitcoinj
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/bitcoinj/wala.input.txt Ljava/util/concurrent/locks/ReentrantLock lock unlock tryLock -- -process-dir bench-repos/harnesses/bitcoinj/build/ -process-dir bench-repos/bitcoinj/core/build/libs/bitcoinj-core-0.16-SNAPSHOT.jar -output-dir Bitcoinj ${SOOT_COMMON_OPTS}

# hadoop
echo "Slicing hadoop"
rm -rf Hadoop
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/hadoop/wala.input.txt Lcom/fasterxml/jackson/core/JsonGenerator '*' -- -process-dir bench-repos/hadoop/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-core/target/hadoop-mapreduce-client-core-3.3.0-SNAPSHOT.jar -process-dir bench-repos/harnesses/hadoop/build/ -output-dir Hadoop ${SOOT_COMMON_OPTS}

# glide
echo "Slicing glide"
rm -rf Glide
${JAVA_HOME}/bin/java -jar ${JAR} bench-repos/harnesses/glide/wala.input.txt Landroid/graphics/Canvas save restore -- -process-dir bench-repos/harnesses/glide/build/ -process-dir bench-repos/harnesses/glide/jars/glide-4.11.0.jar -output-dir Glide ${SOOT_COMMON_OPTS}

#!/bin/bash

# ExoPlayer-Bug
echo "Building ExoPlayer-Bug"
(cd bench-repos/harnesses/ExoPlayer/; ./build-command.sh exoplayer-bug)

# ExoPlayer-Fix
echo "Building ExoPlayer-Fix"
(cd bench-repos/ExoPlayer; git apply ../harnesses/ExoPlayer/hostactivity-fix.patch)
(cd bench-repos/harnesses/ExoPlayer/; ./build-command.sh exoplayer-fix)

# Hystrix Relock
echo "Building Hystrix-Relock"
(cd bench-repos/harnesses/Hystrix/; ./relock-build-command.sh)

# Hystrix Json1
echo "Building Hystrix-Json1"
(cd bench-repos/harnesses/Hystrix/; ./json1-build-command.sh)

# Hystrix Json2
echo "Building Hystrix-Json2"
(cd bench-repos/harnesses/Hystrix/; ./json2-build-command.sh)

# bitcoinj
echo "Building bitcoinj"
(cd bench-repos/harnesses/bitcoinj/; ./build-command.sh)

# hadoop
echo "Building hadoop"
(cd bench-repos/harnesses/hadoop/; ./build-command.sh)

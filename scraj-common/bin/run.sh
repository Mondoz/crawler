#!/bin/bash

source /etc/profile

source ~/.bashrc 

java -cp scraj-common-1.0-SNAPSHOT.jar:lib/* com.hiekn.scraj.common.dedup.DedupNode 1>/dev/null

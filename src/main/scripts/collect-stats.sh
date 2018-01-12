#!/bin/bash
# Collects statistics from SonicMQ into file.

set -e

SONIC_JAR_PATH=${1:-$SONIC_JAR_PATH}
OUTPUT_FILE=${2:-/tmp/sonicmq-monitor-output.json}
CONFIG_FILE=${3:-config.json}

CLASSPATH=$(dirname $0)/zabbix-sonicmq-monitoring-${project.version}.jar
for f in $SONIC_JAR_PATH/*.jar; do
  CLASSPATH=$CLASSPATH:$f
done

java -cp $CLASSPATH com.digia.monitoring.sonicmq.cmd.SonicMQCollect $CONFIG_FILE $OUTPUT_FILE

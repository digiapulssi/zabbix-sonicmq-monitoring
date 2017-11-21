#!/bin/bash
# Updates collected monitoring data if it's older than specified maximum age
set -e

# Obtain file lock
lock=/tmp/$(basename $0).lock
exec 200>$lock
flock -w $SMQ_MON_OUTPUT_MAX_AGE 200

# Check if output file exists and is fresh enough
if [ -e $SMQ_MON_OUTPUT_FILE ]; then
  # ""|| true" prevents script termination even if age is zero
  (( OUTPUT_AGE=$(date +%s) - $(stat $SMQ_MON_OUTPUT_FILE -c %Y) )) || true
  if [ $OUTPUT_AGE -lt $SMQ_MON_OUTPUT_MAX_AGE ]; then
    return 0
  fi
fi

# Set pid
pid=$$
echo $pid 1>&200

# Execute monitor application
$SMQ_MON_INSTALL_PATH/collect-stats.sh $SONICMQ_JAR_PATH $SMQ_MON_OUTPUT_FILE $SMQ_MON_CONFIG_FILE

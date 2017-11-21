#!/bin/bash
# Updates collected monitoring data if it's older than specified maximum age
# Requires that sonicmq_env.sh has been sourced prior to calling script
set -e

sonicmq_get_lock

# Check if output file exists and is fresh enough
if [ -e $SMQ_MON_OUTPUT_FILE ]; then
  # ""|| true" prevents script termination even if age is zero
  (( OUTPUT_AGE=$(date +%s) - $(stat $SMQ_MON_OUTPUT_FILE -c %Y) )) || true
  if [ $OUTPUT_AGE -lt $SMQ_MON_OUTPUT_MAX_AGE ]; then
    return 0
  fi
fi

# Execute monitor application
$SMQ_MON_INSTALL_PATH/collect-stats.sh $SONICMQ_JAR_PATH $SMQ_MON_OUTPUT_FILE $SMQ_MON_CONFIG_FILE

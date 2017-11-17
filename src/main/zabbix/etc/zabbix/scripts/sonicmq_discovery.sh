#!/bin/bash

set -e

COMPONENT=$1

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

# Check update
./sonicmq_update_stats.sh

if [ "$COMPONENT" = "ConnectionAggregate" ]; then
  AGGREGATED=$(cat $SMQ_MON_OUTPUT_FILE | jq '.discovery.Connection.data | map(del(.["{#ID}"])) | map(del(.["{#NAME}"])) | unique // empty')
  echo '{ "data": '$AGGREGATED'}'
else
  cat $SMQ_MON_OUTPUT_FILE | jq '.discovery.'$COMPONENT' // empty'
fi

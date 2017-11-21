#!/bin/bash

set -e

COMPONENT=$1

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh



function sonicmq_get_stat {
  if [ "$COMPONENT" = "ConnectionAggregate" ]; then
    # Filter out ID, NAME and BROKER properties to allow aggregation on remaining elements with unique
    AGGREGATED=$(cat $SMQ_MON_OUTPUT_FILE | jq '.discovery.Connection.data | map(del(.["{#ID}"])) | map(del(.["{#NAME}"])) | map(del(.["{#BROKER}"])) | unique // empty')
    echo '{ "data": '$AGGREGATED'}'
  else
    cat $SMQ_MON_OUTPUT_FILE | jq '.discovery.'$COMPONENT' // empty'
  fi
}

sonicmq_fetch_stat

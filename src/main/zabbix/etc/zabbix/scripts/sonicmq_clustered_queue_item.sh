#!/bin/bash
# Version: 1.0

set -e

QUEUE=$1
ITEM=$2
AGGREGATE=$3

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

function sonicmq_get_stat {
  cat $SMQ_MON_OUTPUT_FILE | jq '[.data.Broker[].items.Queue["'$QUEUE'"].data["'$ITEM'"] // empty] | '$AGGREGATE
}

sonicmq_fetch_stat

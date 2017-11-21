#!/bin/bash

set -e

BROKER=$1
CONNECTION=$2
SUBSCRIBER=$3
ITEM=$4

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

function sonicmq_get_stat {
  cat $SMQ_MON_OUTPUT_FILE | jq '.data.Broker["'$BROKER'"].items.Connection["'$CONNECTION'"].items.Subscriber['$SUBSCRIBER'].data["'$ITEM'"] // empty'
}

sonicmq_fetch_stat

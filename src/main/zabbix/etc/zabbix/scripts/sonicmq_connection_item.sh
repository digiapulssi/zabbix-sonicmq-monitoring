#!/bin/bash
# Version: 1.0

set -e

BROKER=$1
CONNECTION=$2
ITEM=$3

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

function sonicmq_get_stat {
  cat $SMQ_MON_OUTPUT_FILE | jq '.data.Broker["'$BROKER'"].items.Connection["'$CONNECTION'"].data["'$ITEM'"] // empty'
}

sonicmq_fetch_stat

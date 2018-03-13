#!/bin/bash
# Version: 1.0

set -e

AGENT=$1
ITEM=$2

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

function sonicmq_get_stat {
  cat $SMQ_MON_OUTPUT_FILE | jq '.data.Agent["'$AGENT'"].data["'$ITEM'"] // empty'
}

sonicmq_fetch_stat

#!/bin/bash

set -e

AGENTMANAGER=$1
ITEM=$2

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

function sonicmq_get_stat {
  cat $SMQ_MON_OUTPUT_FILE | jq '.data.AgentManager["'$AGENTMANAGER'"].data["'$ITEM'"] // empty'
}

sonicmq_fetch_stat

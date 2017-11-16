#!/bin/bash

set -e

BROKER=$1
ITEM=$2

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

# Check update
./sonicmq_update_stats.sh

cat $SMQ_MON_OUTPUT_FILE | jq '.data.Agent["'$BROKER'"].data["'$ITEM'"]'

#!/bin/bash

set -e

COMPONENT=$1

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

# Check update
./sonicmq_update_stats.sh

cat $SMQ_MON_OUTPUT_FILE | jq .discovery.$COMPONENT

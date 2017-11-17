#!/bin/bash

set -e

HOST=$1
USER=$2
ITEM=$3

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

# Check update
./sonicmq_update_stats.sh

cat $SMQ_MON_OUTPUT_FILE | \
jq '.data.Broker[].items.Connection[]
	| select(.data["connection.Host"] == "'$HOST'" and .data["connection.User"] == "'$USER'")
  | .data["'$ITEM'"]' | \
jq -s add

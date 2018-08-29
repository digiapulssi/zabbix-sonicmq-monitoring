#!/bin/bash
# Version: 1.0

set -e

HOST=$1
USER=$2
TOPIC=$3
ITEM=$4

cd $(dirname $0)

# Load environment
. ./sonicmq_env.sh

function sonicmq_get_stat {
VALUE=$(cat $SMQ_MON_OUTPUT_FILE | \
	jq '.data.Broker[].items.Connection[]
		| select(.data["connection.Host"] == "'$HOST'" and .data["connection.User"] == "'$USER'")
		| .items.TopicSubscription["'$TOPIC'"] // empty
	  | .data["'$ITEM'"]' | \
		jq -s 'max')
if [[ $VALUE -eq null ]]; then
	VALUE=0
	echo $VALUE
fi
echo $VALUE
}

sonicmq_fetch_stat

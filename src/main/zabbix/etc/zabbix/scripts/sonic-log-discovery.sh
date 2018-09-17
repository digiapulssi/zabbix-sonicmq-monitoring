#!/bin/bash
# Version: 1.0
set -e

# Usage: ./sonic-log-discovery.sh <path to discovery list configuration file>
# The configuration file has the following format:
# INTEGRATION|BACKEND|THRESHOLD_COUNT|LOG_FILE|ID|CUSTOMER

CONFIG_FILE="$1"
if [ -z "$CONFIG_FILE" ]; then
  echo "Argument missing"
  exit 1
fi

# The discovery returns a discovery list in zabbix LLD format with the following macros:
#  {#INTEGRATION}
#  {#BACKEND}
#  {#THRESHOLD_COUNT}
#  {#LOG_FILE}
#  {#ID}
#  {#CUSTOMER}

echo -n '{"data":['
LINES=$(cat "$CONFIG_FILE")
while read LINE; do
  if [[ "$LINE" =~ ^(.*)\|(.*)\|(.*)\|(.*)\|(.*)\|(.*)$ ]]; then
    INTEGRATION="${BASH_REMATCH[1]}"
    BACKEND="${BASH_REMATCH[2]}"
    THRESHOLD_COUNT="${BASH_REMATCH[3]}"
    LOG_FILE="${BASH_REMATCH[4]}"
    ID="${BASH_REMATCH[5]}"
    CUSTOMER="${BASH_REMATCH[6]}"

    if [ -z "$NOFIRST" ]; then
      NOFIRST="1"
    else
      echo -n ","
    fi
    echo -n '{"{#INTEGRATION}":"'${INTEGRATION}'","{#BACKEND}":"'${BACKEND}'","{#THRESHOLD_COUNT}":"'${THRESHOLD_COUNT}'","{#LOG_FILE}":"'${LOG_FILE}'","{#ID}":"'${ID}'","{#CUSTOMER}":"'${CUSTOMER}'"}'
  else
    # Error parsing configuration line; skip it
    echo -n ''
  fi
done <<< "$LINES"
echo -n ']}'

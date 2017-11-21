#!/bin/bash
# Environment settings for running SonicMQ Zabbix scripts

# Location of SonicMQ client libraries
SONICMQ_JAR_PATH=/opt/zabbix-sonicmq-monitoring/lib

# Installation path of monitor application
SMQ_MON_INSTALL_PATH=/opt/zabbix-sonicmq-monitoring

# Monitor output file
SMQ_MON_OUTPUT_FILE=/tmp/sonicmq-monitor-output.json

# Monitor configuration file, configures SonicMQ connection and monitor application
SMQ_MON_CONFIG_FILE=$SMQ_MON_INSTALL_PATH/config.json

# Maximum monitor output file age in seconds
SMQ_MON_OUTPUT_MAX_AGE=120

# Run mode: in cron mode scripts do not trigger updates and cron job is expected to be setup for it
SMQ_MON_CRON_MODE=0

# Monitor application log
# Define either absolute path or special value "syslog" to redirect to syslog
SMQ_MON_LOG=syslog

echo $SMQ_MON_LOG

# Function providing common fetch logic around script specific logic parsing the output
function sonicmq_fetch_stat {
  if [ $SMQ_MON_CRON_MODE -eq 0 ]; then
    if [ "$SMQ_MON_LOG" = "syslog" ]; then
      . ./sonicmq_update_stats.sh 2>&1 | logger
    else
    	. ./sonicmq_update_stats.sh 2>&1 >>$SMQ_MON_LOG
    fi
  fi

  if [ -e $SMQ_MON_OUTPUT_FILE ]; then
    # Get data from output (implemented by calling script)
    sonicmq_get_stat
  else
  	echo "ERROR: Statistics output file missing."
  fi
}

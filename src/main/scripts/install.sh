#!/bin/bash
set -e

VERSION=${project.version}

# Configuration properties
read -p "Enter monitor application install location (default /opt): " input
MONITOR_INSTALL_DIRECTORY=${input:-/opt}

read -p "Enter SonicMQ client jar path: " SONICMQ_JAR_PATH

SMQ_MON_INSTALL_PATH=$MONITOR_INSTALL_DIRECTORY/zabbix-sonicmq-monitoring-$VERSION

# Unpack monitor
tar xzf zabbix-sonicmq-monitoring-$VERSION-release.tar.gz -C $MONITOR_INSTALL_DIRECTORY
cp -p $SMQ_MON_INSTALL_PATH/config-sample.json $SMQ_MON_INSTALL_PATH/config.json
chown -R root:zabbix $SMQ_MON_INSTALL_PATH

# Unpack Zabbix scripts
mkdir -p /etc/zabbix
tar xzf zabbix-sonicmq-monitoring-$VERSION-scripts.tar.gz -C /etc/zabbix
chown root:zabbix /etc/zabbix/scripts/sonicmq*.sh
chown root:zabbix /etc/zabbix/conf.d/sonicmq-monitoring.conf

sed -i "s|^SONICMQ_JAR_PATH=.*$|SONICMQ_JAR_PATH=$SONICMQ_JAR_PATH|" /etc/zabbix/scripts/sonicmq_env.sh
sed -i "s|^SMQ_MON_INSTALL_PATH=.*$|SMQ_MON_INSTALL_PATH=$SMQ_MON_INSTALL_PATH|" /etc/zabbix/scripts/sonicmq_env.sh

echo "Monitor application and Zabbix scripts installed."
echo "Configure SonicMQ connection properties to $SMQ_MON_INSTALL_PATH/config.json."
echo "Zabbix script configuration can be further customized by changing /etc/zabbix/scripts/sonicmq_env.sh"

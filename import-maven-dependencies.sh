#!/bin/bash
# Imports SonicMQ dependencies into local maven repository

set -e

if [ $# -ne 2 ]; then
	echo "Usage: $(basename $0) <sonicmq-jars-path> <sonicmq-version>"
	exit 1
fi

SONICMQ_LIBS_PATH=$1
SONICMQ_VERSION=$2

function install_file() {
	mvn install:install-file -Dfile=$SONICMQ_LIBS_PATH/$1 -DgroupId=sonicmq -DartifactId=$2 -Dversion=$SONICMQ_VERSION -Dpackaging=jar
}

install_file broker.jar broker
install_file mfcontext.jar mfcontext
install_file mgmt_client.jar mgmt_client
install_file mgmt_config.jar mgmt_config
install_file sonic_Client.jar sonic_Client
install_file sonic_Crypto.jar sonic_Crypto
install_file sonic_mgmt_client.jar sonic_mgmt_client

zabbix-sonicmq-monitoring
=========================

SonicMQ server monitoring for Zabbix. Partly inspired by similar AppDynamics
extension module (GitHub: https://github.com/Appdynamics/sonicmq-monitoring-extension).

## Building the Source ##

### Requirements ###

1. Java 1.7 SDK or later
2. Maven 3
3. SonicMQ jar packages for desired version, at SonicMQ version 8.5.1 list of jar packages is:
   * broker.jar
   * mfcontext.jar
   * mgmt_client.jar
   * mgmt_config.jar
   * sonic_Client.jar
   * sonic_Crypto.jar
   * sonic_mgmt_client.jar

### Building ###

1. Obtain required SonicMQ jar packages and install them to your local Maven. This needs to be done only once.
   * On Unix this can be done using provided script: `./import-maven-dependencies.sh <sonicmq-jars-path> <sonicmq-version>
2. Update the sonicmq.version property in pom.xml if necessary
3. Build: `mvn clean install assembly:single`

The build produces tar.gz binary packages and install.sh in /target directory.

## Installation ##

Target machine should have the following pre-installed:
* SonicMQ client libraries listed above
* Java 1.7 JRE or later
* Zabbix agent
* jq

1. Copy the zabbix-sonicmq-monitoring-<version>-release.tar.gz, zabbix-sonicmq-monitoring-<version>-scripts.tar.gz and install.sh from release into target machine.
2. Run install.sh as root and give install path for monitor application and path to SonicMQ client jars when prompted.
3. Configure SonicMQ connection properties in <monitor-install-path>/zabbix-sonicmq-monitoring-<version>/config.json.

Script installs both monitor application and Zabbix scripts and configuration.
Zabbix scripts and configuration are installed according to preferred agent
setup in /etc/zabbix/scripts and /etc/zabbix/zabbix_agentd.d respectively.

## Configuration Options ##

### Monitor Output Refresh with Zabbix Scripts ###

By default, Zabbix scripts run the monitor application if the output data is
older than 2 minutes (120s). This can be modified by editing
SMQ_MON_OUTPUT_MAX_AGE value in /etc/zabbix/scripts/sonicmq_env.sh.

Alternatively you can change SMQ_MON_CRON_MODE in same file to 1 to disable
running monitor from Zabbix scripts and configure script
/etc/zabbix/scripts/sonicmq_update_stats.sh to run from cron job instead. In
this mode, SMQ_MON_OUTPUT_MAX_AGE should also be set to 0 (i.e. run always) to
make monitor running fully controlled by cron timing.

Suitable command for running from cron is:
`. /etc/zabbix/scripts/sonicmq_env.sh && . /etc/zabbix/scripts/sonicmq_update_stats.sh`

### Monitor Logging ###

By default, Zabbix scripts redirect the logging from monitor application script
to syslog. To change this to go in separate file change the SMQ_MON_LOG property
to absolute log file path.

## Running ##

### Running the Monitor Standalone ###

To run the monitor to just generate JSON output switch to unpack directory and execute:

`./collect-stats.sh <sonicmq-jar-path> <output-file> <config-file>`

JAR path should point to directory containing SonicMQ jar packages.

All arguments can be left out depending on conditions:
* sonicmq-jar-path is not needed if there is SONICMQ_JAR_PATH environment variable set that points to necessary JAR directory
* output-file is not mandatory
* config-file is not mandatory

## Usage ##

### Brokers ###
Item Syntax | Description | Units |
----------- | ----------- | ----- |
sonicmq.broker.discovery | Discover brokers | Provides template variables: {#NAME} |
sonicmq.broker.state[{#NAME}] | Broker state | Integer, raw value from SonicMQ |
sonicmq.broker.state_name[{#NAME}] | Broker state name | String |
sonicmq.broker.replication_state[{#NAME}] | Broker replication state | String |
sonicmq.broker.replication_type[{#NAME}] | Broker replication type | String |
sonicmq.broker.topicdb_size[{#NAME}] | Topic DB size | |
sonicmq.broker.bytes.received_s[{#NAME}] |  Bytes received/s | |
sonicmq.broker.bytes.delivered_s[{#NAME}] | Bytes delivered/s | |
sonicmq.broker.messages.received_s[{#NAME}] | Messages received/s | |
sonicmq.broker.messages.delivered_s[{#NAME}] | Messages delivered/s | |
sonicmq.broker.messages.delivered[{#NAME}] | Messages delivered | |
sonicmq.broker.messages.received[{#NAME}] | Messages received | |
sonicmq.broker.connections.count[{#NAME}] | Active connection count | |
sonicmq.broker.connections.rejected_m[{#NAME}] | Rejected connections/min | |

### Agents (Containers) ###
Item Syntax | Description | Units |
----------- | ----------- | ----- |
sonicmq.agent.discovery | Discover agents (containers) | Provides template variables: {#NAME} |
sonicmq.agent.state[{#NAME}] | Agent state | Integer, raw value from SonicMQ |
sonicmq.agent.state_name[{#NAME}] | Agent state name | String |
sonicmq.agent.memory.max_usage[{#NAME}] | Maximum memory usage (bytes) | |
sonicmq.agent.memory.current_usage[{#NAME}] | Current memory usage (bytes) | |
sonicmq.agent.threads.pool_waits[{#NAME}] | Thread pool waits | |
sonicmq.agent.threads.max_pool_size[{#NAME}] | Maximum thread pool size | |
sonicmq.agent.threads.current_total[{#NAME}] | Current thread count | |
sonicmq.agent.threads.current_pool_size[{#NAME}] | Current thread pool size | |
sonicmq.agent.notifications.awaiting_dispatch[{#NAME}] | Number of notifications awaiting dispatch | |
sonicmq.agent.notifications.dropped[{#NAME}] | Number of notifications dropped | |
sonicmq.agent.notifications.max_awaiting_dispatch[{#NAME}] | Maximum number of notifications awaiting dispatch | |

### Agent Manager ###
Item Syntax | Description | Units |
----------- | ----------- | ----- |
sonicmq.agent_manager.discovery | Discover agent managers | Provides template variables: {#NAME} |
sonicmq.agentmanager.max_pool_size[{#NAME}] | Maximum pool size | |
sonicmq.agentmanager.current_pool_size[{#NAME}] | Current pool size | |
sonicmq.agentmanager.pool_waits[{#NAME}] | Pool waits | |

### Connections ###
Item Syntax | Description | Units |
----------- | ----------- | ----- |
sonicmq.connection.discovery | Discover connections | Provides template variables: {#NAME} {#BROKER} {#HOST} {#USER} {#ID} |
sonicmq.connection.bytes.delivered[{#BROKER},{#ID}] | Bytes delivered | |
sonicmq.connection.bytes.delivered_s[{#BROKER},{#ID}] | Bytes delivered/s | |
sonicmq.connection.bytes.received[{#BROKER},{#ID}] | Bytes received | |
sonicmq.connection.bytes.received_s[{#BROKER},{#ID}] | Bytes received/s | |
sonicmq.connection.messages.delivered[{#BROKER},{#ID}] | Messages delivered | |
sonicmq.connection.messages.delivered_s[{#BROKER},{#ID}] | Messages delivered/s | |
sonicmq.connection.messages.received[{#BROKER},{#ID}] | Messages received | |
sonicmq.connection.messages.received_s[{#BROKER},{#ID}] | Messages received/s | |
sonicmq.connection_aggregate.discovery | Discover aggregated connections by host/user (i.e. all connections by user from same host) | Provides template variables: {#HOST} {#USER} |
sonicmq.connection_aggregate.bytes.delivered[{#HOST},{#USER}] | Bytes delivered for all connections from host/user | |
sonicmq.connection_aggregate.bytes.delivered_s[{#HOST},{#USER}] | Bytes delivered/s for all connections from host/user | |
sonicmq.connection_aggregate.bytes.received[{#HOST},{#USER}] | Bytes received for all connections from host/user | |
sonicmq.connection_aggregate.bytes.received_s[{#HOST},{#USER}] | Bytes received/s for all connections from host/user | |
sonicmq.connection_aggregate.messages.delivered[{#HOST},{#USER}] | Messages delivered for all connections from host/user | |
sonicmq.connection_aggregate.messages.delivered_s[{#HOST},{#USER}] | Messages delivered/s for all connections from host/user | |
sonicmq.connection_aggregate.messages.received[{#HOST},{#USER}] | Messages received for all connections from host/user | |
sonicmq.connection_aggregate.messages.received_s[{#HOST},{#USER}] | Messages received/s for all connections from host/user | |

### Queues ###
Item Syntax | Description | Units |
----------- | ----------- | ----- |
sonicmq.queue.discovery | Discover queues | Provides template variables {#NAME} {#BROKER} {#ID} |
sonicmq.queue.max_age[{#BROKER},{#ID}] | Maximum message age in queue | |
sonicmq.queue.size[{#BROKER},{#ID}] | Size of messages in queue | |
sonicmq.queue.delivered_s[{#BROKER},{#ID}] | Messages delivered/s | |
sonicmq.queue.count[{#BROKER},{#ID}] | Number of messages in queue | |
sonicmq.queue.time_in_queue[{#BROKER},{#ID}] | Message time in queue | |
sonicmq.queue.received_s[{#BROKER},{#ID}] | Messages received/s | |
sonicmq.queue.max_depth[{#BROKER},{#ID}] | Queue maximum depth | |

### Topic Subscriptions ###

These items can be used to check if some subscription to topic is stuck causing messages to pile up. Statistics collected by Java application include largest values
for each connection/topic. Aggregated items merge all values by host/user into single maximum value.

Item Syntax | Description | Units |
----------- | ----------- | ----- |
sonicmq.topicsubscription.discovery | Discover topic subscriptions | Provides template variables {#NAME} {#HOST} {#USER} {#ID} {#CONNECTIONID} {#BROKER} |
sonicmq.topicsubscription.message_count[{#BROKER},{#CONNECTIONID},{#ID}] | Number of messages waiting in topic for connection | |
sonicmq.topicsubscription.message_size[{#BROKER},{#CONNECTIONID},{#ID}] | Size of messages waiting in topic for connection | |
sonicmq.topicsubscription_aggregate.discovery | Discover aggregated topic subscriptions | Provides template variables {#NAME} {#HOST} {#USER} {#ID} |
sonicmq.topicsubscription_aggregate.message_count[{#HOST},{#USER},{#ID}] | Number of messages waiting in topic for host/user | |
sonicmq.topicsubscription_aggregate.message_size[{#HOST},{#USER},{#ID}] | Size of messages waiting in topic for host/user | |

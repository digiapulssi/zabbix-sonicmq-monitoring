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
setup in /etc/zabbix/scripts and /etc/zabbix/conf.d respectively.

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
* output-file is not mandatory, if not given it will be written to /tmp/sonicmq-monitor-output.json
* config-file is not mandatory, if not given it will read config.json from current working directory

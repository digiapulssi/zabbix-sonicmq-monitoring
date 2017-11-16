zabbix-sonicmq-monitoring
=========================

SonicMQ server monitoring for Zabbix. Partly inspired by similar AppDynamics 
extension module (GitHub: https://github.com/Appdynamics/sonicmq-monitoring-extension).

## Usage ##



## Building the Source ##

### Requirements ###

1. Java 1.7 or later
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

## Installation ##


## Running ##

### Running the Monitor Standalone ###

To run the monitor to just generate JSON output switch to unpack directory and execute:

`./collect-stats.sh <sonicmq-jar-path> <output-file> <config-file>`

JAR path should point to directory containing SonicMQ jar packages.

All arguments can be left out depending on conditions:
* sonicmq-jar-path is not needed if there is SONICMQ_JAR_PATH environment variable set that points to necessary JAR directory
* output-file is not mandatory, if not given it will be written to /tmp/sonicmq-monitor-output.json
* config-file is not mandatory, if not given it will read config.json from current working directory

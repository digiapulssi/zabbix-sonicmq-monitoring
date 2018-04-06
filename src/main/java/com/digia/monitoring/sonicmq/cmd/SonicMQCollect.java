package com.digia.monitoring.sonicmq.cmd;

import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digia.monitoring.sonicmq.SonicMQMonitoringException;
import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;
import com.digia.monitoring.sonicmq.monitor.SonicMQMonitor;
import com.digia.monitoring.sonicmq.monitor.SonicMQMonitorConfiguration;

/**
 * Main class that connects to SonicMQ, executes discovery and collection and output results to JSON file.
 * @author Sami Pajunen
 */
public class SonicMQCollect {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SonicMQCollect.class);
    
    public static void main(String[] args) {
        String configFile = args[0];
        String outputFile = args[1];
        boolean pretty = true;
        
        // Setup monitor
        SonicMQMonitor monitor;
        try {
        	LOGGER.debug("Loading configuration file {}.", configFile);
            SonicMQMonitorConfiguration config = SonicMQMonitorConfiguration.load(configFile);
            monitor = new SonicMQMonitor(config);
        } catch (SonicMQMonitoringException ex) {
        	LOGGER.error("Error creating monitor.", ex);
        	System.exit(1);
        	return;
        } catch (IOException ex) {
            LOGGER.error("Unable to read configuration.", ex);
            System.exit(1);
            return;
        }
        
        // Collect data
        SonicMQMonitoringData data = new SonicMQMonitoringData(monitor.getDomain());
        try {
        	LOGGER.debug("Discovering components...");
            monitor.discoverComponents();
            LOGGER.debug("Discovering connections and subscribers...");
            monitor.discoverConnectionsAndSubscribers();
            LOGGER.debug("Discovering queues...");
            monitor.discoverQueues();
            LOGGER.debug("Collecting discovered items...");
            monitor.collectDiscoveryItems(data);
            LOGGER.debug("Collecting configuration items...");
            monitor.collectConfigurationData(data);
            LOGGER.debug("Collecting metrics data...");
            monitor.collectMetricsData(data);
            //LOGGER.debug("Collecting connection data...");
            //monitor.collectConnectionData(data);
        } finally {
            monitor.close();
        }
        
        // Write output
        try {
        	LOGGER.debug("Writing to output file {}.", outputFile);
            FileWriter out = new FileWriter(outputFile);
            try {
                data.toJson(out, pretty);
            } finally {
                out.close();
            }
        } catch (IOException ex) {
            LOGGER.error("Unable to write output file.", ex);
        }
    }
}

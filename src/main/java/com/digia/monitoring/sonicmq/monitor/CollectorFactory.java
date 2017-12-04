package com.digia.monitoring.sonicmq.monitor;

import com.digia.monitoring.sonicmq.ICollector;
import com.digia.monitoring.sonicmq.SonicMQMonitoringException;

/**
 * Static factory class for creating ICollector implementations.
 * @author Sami Pajunen
 */
public class CollectorFactory {

    /**
     * Creates collector by its name. Collector name is expected to be fully qualified Java class name and the
     * class should have accessible no-arg constructor.
     * @param collectorName Collector name
     * @return Collector instance
     * @throws SonicMQMonitoringException Thrown if creating collector instance fails
     */
    public static ICollector createCollector(String collectorName) throws SonicMQMonitoringException {
        try {
            return (ICollector) Class.forName(collectorName).newInstance();
        } catch (InstantiationException ex) {
            throw new SonicMQMonitoringException("Unable to create collector instance [" + collectorName + "].", ex);
        } catch (IllegalAccessException ex) {
            throw new SonicMQMonitoringException("Unable to create collector instance [" + collectorName + "].", ex);
        } catch (ClassNotFoundException ex) {
            throw new SonicMQMonitoringException("Unable to create collector instance [" + collectorName + "].", ex);
        }
    }
}

package com.digia.monitoring.sonicmq;

import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;

/**
 * Defines interface for data collectors invoked from {@link SonicMQMonitor}.
 * @author Sami Pajunen
 * @see SonicMQMonitor
 */
public interface ICollector {
    /**
     * Collects data for given component.
     * @param clientProxyFactory Client proxy factory
     * @param component Component
     * @param data Data collection
     */
    public void collectData(IClientProxyFactory clientProxyFactory, ISonicMQComponent component, SonicMQMonitoringData data);
}

package com.digia.monitoring.sonicmq.collector;

import com.digia.monitoring.sonicmq.DiscoveryItemClass;
import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;
import com.sonicsw.mf.common.metrics.IMetric;
import com.sonicsw.mf.common.metrics.IMetricIdentity;
import com.sonicsw.mf.mgmtapi.runtime.IAgentManagerProxy;

/**
 * Collector implementation for agent manager.
 * @author Sami Pajunen
 */
public class AgentManagerCollector extends CollectorBase {
    /** List of metrics to check. */
    private static final IMetricIdentity[] metricIds = new IMetricIdentity[] {
            IAgentManagerProxy.SYSTEM_POLLTHREADS_CURRENTPOOLSIZE_METRIC_ID ,
            IAgentManagerProxy.SYSTEM_POLLTHREADS_MAXPOOLSIZE_METRIC_ID ,
            IAgentManagerProxy.SYSTEM_POLLTHREADS_POOLWAITS_METRIC_ID
    };

    public void collectAgentManagerData(IAgentManagerProxy proxy, String name, SonicMQMonitoringData data) {
        IMetricIdentity[] activeMetrics = proxy.getActiveMetrics(metricIds);
        IMetric[] metrics = proxy.getMetricsData(activeMetrics, false).getMetrics();
        for (IMetric m : metrics) {
            data.addData(DiscoveryItemClass.AgentManager, name, m.getMetricIdentity().getAbsoluteName(), m.getValue());
        }
    }
}

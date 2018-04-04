package com.digia.monitoring.sonicmq.collector;

import com.digia.monitoring.sonicmq.DiscoveryItemClass;
import com.digia.monitoring.sonicmq.ISonicMQComponent;
import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;
import com.sonicsw.mf.common.metrics.IMetric;
import com.sonicsw.mf.common.metrics.IMetricIdentity;
import com.sonicsw.mf.mgmtapi.runtime.IAgentProxy;

/**
 * Collector implementation for agent.
 * @author Sami Pajunen
 */
public class AgentCollector extends CollectorBase {

    /** List of metrics to check. */
    private static final IMetricIdentity[] metricIds = new IMetricIdentity[] {
            IAgentProxy.SYSTEM_MEMORY_MAXUSAGE_METRIC_ID,
            IAgentProxy.SYSTEM_MEMORY_CURRENTUSAGE_METRIC_ID,
            IAgentProxy.SYSTEM_THREADS_CURRENTTOTAL_METRIC_ID,
            IAgentProxy.SYSTEM_THREADS_CURRENTPOOLSIZE_METRIC_ID,
            IAgentProxy.SYSTEM_THREADS_MAXPOOLSIZE_METRIC_ID,
            IAgentProxy.SYSTEM_THREADS_POOLWAITS_METRIC_ID,
            IAgentProxy.SYSTEM_NOTIFICATIONS_AWAITINGDISPATCH_METRIC_ID ,
            IAgentProxy.SYSTEM_NOTIFICATIONS_DROPPED_METRIC_ID ,
            IAgentProxy.SYSTEM_NOTIFICATIONS_MAXAWAITINGDISPATCH_METRIC_ID
    };
    
    private static final String AGENT_STATE = "agent.State";
    private static final String AGENT_STATE_NAME = "agent.StateName";

    public void collectAgentData(IAgentProxy proxy, ISonicMQComponent component, 
            SonicMQMonitoringData data) {
        String name = component.getName();
        data.addData(DiscoveryItemClass.Agent, name, AGENT_STATE, String.valueOf(component.getState()));
        data.addData(DiscoveryItemClass.Agent, name, AGENT_STATE_NAME, component.getStateName());
        if (component.isOnline()) {
            IMetricIdentity[] activeMetrics = proxy.getActiveMetrics(metricIds);
            IMetric[] metrics = proxy.getMetricsData(activeMetrics, false).getMetrics();
            for (IMetric m : metrics) {
                data.addData(DiscoveryItemClass.Agent, name, m.getMetricIdentity().getAbsoluteName(), m.getValue());
            }
        }
    }

}

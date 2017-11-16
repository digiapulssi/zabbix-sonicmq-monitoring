package com.digia.monitoring.sonicmq.collector;

import com.digia.monitoring.sonicmq.IClientProxyFactory;
import com.digia.monitoring.sonicmq.ICollector;
import com.digia.monitoring.sonicmq.ISonicMQComponent;
import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;
import com.sonicsw.mf.mgmtapi.runtime.IAgentManagerProxy;
import com.sonicsw.mf.mgmtapi.runtime.IAgentProxy;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;

/**
 * Base class providing template methods for different component types.
 * @author Sami Pajunen
 */
public class CollectorBase implements ICollector {
    public final void collectData(IClientProxyFactory clientProxyFactory, ISonicMQComponent component, 
            SonicMQMonitoringData data) {
        switch (component.getType()) {
        case AGENT:
        	IAgentProxy agentProxy = clientProxyFactory.getAgentProxy(component.getJmxName());
            collectAgentData(agentProxy, component.getName(), data);
            break;
        case AGENT_MANAGER:
        	IAgentManagerProxy agentManagerProxy = clientProxyFactory.getAgentManagerProxy(component.getJmxName());
            collectAgentManagerData(agentManagerProxy, component.getName(), data);
            break;
        case BROKER:
        	IBrokerProxy brokerProxy = clientProxyFactory.getBrokerProxy(component.getJmxName());
            collectBrokerData(brokerProxy, component.getName(), data);
            break;
        }
    }
    
    /**
     * Collects agent data. Invoked only if component is agent.
     * @param proxy Agent proxy
     * @param name Agent name
     * @param data Data collection
     */
    protected void collectAgentData(IAgentProxy proxy, String name, SonicMQMonitoringData data) {
    }

    /**
     * Collects agent manager data. Invoked only if component is agent manager.
     * @param proxy Agent manager proxy
     * @param name Agent manager name
     * @param data Data collection
     */
    protected void collectAgentManagerData(IAgentManagerProxy proxy, String name, SonicMQMonitoringData data) {
    }

    /**
     * Collects broker data. Invoked only if component is broker.
     * @param proxy Broker proxy
     * @param name Broker name
     * @param data Data collection
     */
    protected void collectBrokerData(IBrokerProxy proxy, String name, SonicMQMonitoringData data) {
    }
}

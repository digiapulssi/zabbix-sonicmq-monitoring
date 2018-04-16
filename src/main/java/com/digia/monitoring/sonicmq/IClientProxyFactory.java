package com.digia.monitoring.sonicmq;

import java.io.Closeable;

import com.sonicsw.ma.mgmtapi.config.MgmtException;
import com.sonicsw.mf.mgmtapi.runtime.IAgentManagerProxy;
import com.sonicsw.mf.mgmtapi.runtime.IAgentProxy;
import com.sonicsw.mq.mgmtapi.config.IBrokerBean;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;

/**
 * Provides methods for creating SonicMQ proxies.
 * @author Sami Pajunen
 */
public interface IClientProxyFactory extends Closeable {

    /**
     * Returns "global" domain level agent manager proxy.
     * @return Agent manager proxy instance
     */
    IAgentManagerProxy getDomainLevelAgentManagerProxy();

    /**
     * Returns broker proxy by broker's canonical (JMX) name.
     * @param jmxName Canonical name
     * @return Broker proxy
     */
    IBrokerProxy getBrokerProxy(String jmxName);

    /**
     * Returns agent manager proxy by agent manager's canonical (JMX) name.
     * @param jmxName Canonical name
     * @return Agent manager proxy
     */
    IAgentManagerProxy getAgentManagerProxy(String jmxName);

    /**
     * Returns agent proxy by agent's canonical (JMX) name.
     * @param jmxName Canonical name
     * @return Agent proxy
     */
    IAgentProxy getAgentProxy(String jmxName);
    
    /**
     * Return broker configuration bean.
     * @param broker Broker name
     * @return Broker configuration bean
     * @throws MgmtException Thrown if obtaining the bean fails
     */
    public IBrokerBean getBrokerBean(String broker) throws MgmtException;
}
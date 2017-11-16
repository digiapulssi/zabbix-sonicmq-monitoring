package com.digia.monitoring.sonicmq.monitor;

import java.io.Closeable;
import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digia.monitoring.sonicmq.IClientProxyFactory;
import com.sonicsw.mf.jmx.client.JMSConnectorAddress;
import com.sonicsw.mf.jmx.client.JMSConnectorClient;
import com.sonicsw.mf.mgmtapi.runtime.IAgentManagerProxy;
import com.sonicsw.mf.mgmtapi.runtime.IAgentProxy;
import com.sonicsw.mf.mgmtapi.runtime.MFProxyFactory;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;
import com.sonicsw.mq.mgmtapi.runtime.MQProxyFactory;

/**
 * <p>Manages JMS connection to SonicMQ providing access to proxy interfaces that utilize the connection.</p>
 * 
 * <p>Because the factory holds connection to SonicMQ the connection must be closed by invoking {@link #close()}.</p>
 * 
 * @author Sami Pajunen
 */
class ClientProxyFactory implements Closeable, IClientProxyFactory {
    
    /** Property specifying connection address for JMS connector client. */
    public static final String CONNECTION_URLS = "ConnectionURLs";
    /** Property specifying user for JMS connector client. */
    public static final String DEFAULT_USER = "DefaultUser";
    /** Property specifying password for JMS connector client. */
    public static final String DEFAULT_PASSWORD = "DefaultPassword";
    
    /** SonicMQ connector client. */
    private JMSConnectorClient client;
    
    private Logger logger = LoggerFactory.getLogger(ClientProxyFactory.class);

    /**
     * Creates new ClientProxyFactory instance and connects to
     * @param location Connection URL
     * @param username Username
     * @param password Password
     * @param timeout Connection timeout
     */
    public ClientProxyFactory(String location, String username, String password, long timeout) {
        client = new JMSConnectorClient();
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(CONNECTION_URLS, location);
        env.put(DEFAULT_USER, username);
        env.put(DEFAULT_PASSWORD, password);
    	logger.debug("Connecting to {}", location);
        client.connect(new JMSConnectorAddress(env), timeout);
    }
    
    /* (non-Javadoc)
     * @see com.digia.monitoring.sonicmq.monitor.IClientProxyFactory#getDomainLevelAgentManagerProxy(java.lang.String)
     */
    @Override
    public IAgentManagerProxy getDomainLevelAgentManagerProxy(String domain) {
        String jmxName = domain + "." + IAgentManagerProxy.GLOBAL_ID + ":ID=" + IAgentManagerProxy.GLOBAL_ID;
        return getAgentManagerProxy(jmxName);
    }

    /* (non-Javadoc)
     * @see com.digia.monitoring.sonicmq.monitor.IClientProxyFactory#getBrokerProxy(java.lang.String)
     */
    @Override
    public IBrokerProxy getBrokerProxy(String jmxName) {
        return MQProxyFactory.createBrokerProxy(client, newObjectName(jmxName));
    }
    
    /* (non-Javadoc)
     * @see com.digia.monitoring.sonicmq.monitor.IClientProxyFactory#getAgentManagerProxy(java.lang.String)
     */
    @Override
    public IAgentManagerProxy getAgentManagerProxy(String jmxName) {
        return MFProxyFactory.createAgentManagerProxy(client, newObjectName(jmxName));
    }

    /* (non-Javadoc)
     * @see com.digia.monitoring.sonicmq.monitor.IClientProxyFactory#getAgentProxy(java.lang.String)
     */
    @Override
    public IAgentProxy getAgentProxy(String jmxName) {
        return MFProxyFactory.createAgentProxy(client, newObjectName(jmxName));
    }
    
    /**
     * Closes the underlying connection to SonicMQ. Closing the factory also invalidates all created proxies.
     */
    public void close() {
        client.disconnect();
    }

    /**
     * Creates JMX object name from string.
     * @param jmxName JMX name string
     * @return Object name
     */
    private static ObjectName newObjectName(String jmxName) {
        try {
            return new ObjectName(jmxName);
        } catch (MalformedObjectNameException ex) {
            throw new IllegalArgumentException("Invalid JMX object name.", ex);
        }
    }

}

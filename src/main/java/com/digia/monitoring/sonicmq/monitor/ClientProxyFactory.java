package com.digia.monitoring.sonicmq.monitor;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digia.monitoring.sonicmq.IClientProxyFactory;
import com.sonicsw.ma.mgmtapi.config.MgmtException;
import com.sonicsw.mf.jmx.client.JMSConnectorAddress;
import com.sonicsw.mf.jmx.client.JMSConnectorClient;
import com.sonicsw.mf.mgmtapi.runtime.IAgentManagerProxy;
import com.sonicsw.mf.mgmtapi.runtime.IAgentProxy;
import com.sonicsw.mf.mgmtapi.runtime.MFProxyFactory;
import com.sonicsw.mq.mgmtapi.config.IBrokerBean;
import com.sonicsw.mq.mgmtapi.config.MQMgmtBeanFactory;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;
import com.sonicsw.mq.mgmtapi.runtime.MQProxyFactory;

/**
 * <p>Manages JMS connection to SonicMQ providing access to proxy interfaces that utilize the connection.</p>
 * 
 * <p>Because the factory holds connection to SonicMQ the connection must be closed by invoking {@link #close()}.</p>
 * 
 * @author Sami Pajunen
 */
class ClientProxyFactory implements IClientProxyFactory {
    
    /** Property specifying connection address for JMS connector client. */
    public static final String CONNECTION_URLS = "ConnectionURLs";
    /** Property specifying user for JMS connector client. */
    public static final String DEFAULT_USER = "DefaultUser";
    /** Property specifying password for JMS connector client. */
    public static final String DEFAULT_PASSWORD = "DefaultPassword";
    
    /** SonicMQ connector client. */
    private JMSConnectorClient client;
    
    /** SonicMQ management bean factory. */
    private MQMgmtBeanFactory mgmtBeanFactory;
    
    /** Map of broker beans, populated on demand. */
    private Map<String, IBrokerBean> brokerBeans = new HashMap<>();
    
    /** Domain. */
    private String domain;
    
    /** Location URL. */
    private String location;
    
    /** Username. */
    private String username;
    
    /** Password. */
    private String password;
    
    private Logger logger = LoggerFactory.getLogger(ClientProxyFactory.class);

    /**
     * Creates new ClientProxyFactory instance and connects to
     * @param domain Domain
     * @param location Connection URL
     * @param username Username
     * @param password Password
     * @param timeout Connection timeout
     */
    public ClientProxyFactory(String domain, String location, String username, String password, long timeout) {
        this.domain = domain;
        this.location = location;
        this.username = username;
        this.password = password;
        
        client = new JMSConnectorClient();
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(CONNECTION_URLS, location);
        env.put(DEFAULT_USER, username);
        env.put(DEFAULT_PASSWORD, password);
        logger.debug("Connecting to {}", location);
        client.connect(new JMSConnectorAddress(env), timeout);
    }
    
    public IBrokerBean getBrokerBean(String broker) throws MgmtException {
        populateBrokerBeans();
        return brokerBeans.get(broker);
    }
    
    @Override
    public IAgentManagerProxy getDomainLevelAgentManagerProxy() {
        String jmxName = domain + "." + IAgentManagerProxy.GLOBAL_ID + ":ID=" + IAgentManagerProxy.GLOBAL_ID;
        return getAgentManagerProxy(jmxName);
    }
    
    @Override
    public IBrokerProxy getBrokerProxy(String jmxName) {
        return MQProxyFactory.createBrokerProxy(client, newObjectName(jmxName));
    }
    
    @Override
    public IAgentManagerProxy getAgentManagerProxy(String jmxName) {
        return MFProxyFactory.createAgentManagerProxy(client, newObjectName(jmxName));
    }

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
    
    private void populateBrokerBeans() throws MgmtException {
        // Bean name is not same as broker name, bean name includes folder structure
        // So this creates lookup table that works with just broker name
        MQMgmtBeanFactory factory = getMgmtBeanFactory();
        for (Object beanName : factory.getBrokerBeanNames()) {
            IBrokerBean bean = factory.getBrokerBean((String) beanName);
            brokerBeans.put(bean.getBrokerName(), bean);
        }
    }

    private MQMgmtBeanFactory getMgmtBeanFactory() throws MgmtException {
        if (mgmtBeanFactory == null) {
            MQMgmtBeanFactory factory = new MQMgmtBeanFactory();
            factory.connect(domain, location, username, password);
            this.mgmtBeanFactory = factory;
        }
        return mgmtBeanFactory;
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

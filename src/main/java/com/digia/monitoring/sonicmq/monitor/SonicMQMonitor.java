package com.digia.monitoring.sonicmq.monitor;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digia.monitoring.sonicmq.ComponentType;
import com.digia.monitoring.sonicmq.DiscoveryItemClass;
import com.digia.monitoring.sonicmq.ICollector;
import com.digia.monitoring.sonicmq.ISonicMQComponent;
import com.digia.monitoring.sonicmq.SonicMQMonitoringException;
import com.digia.monitoring.sonicmq.model.ConnectionDiscoveryItem;
import com.digia.monitoring.sonicmq.model.ConnectionMemberData;
import com.digia.monitoring.sonicmq.model.DiscoveryItemData;
import com.digia.monitoring.sonicmq.model.NamedDiscoveryItem;
import com.digia.monitoring.sonicmq.model.QueueDiscoveryItem;
import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;
import com.digia.monitoring.sonicmq.model.SubscriberData;
import com.digia.monitoring.sonicmq.model.TopicSubscriptionDiscoveryItem;
import com.sonicsw.ma.mgmtapi.config.MgmtException;
import com.sonicsw.mf.common.runtime.IComponentState;
import com.sonicsw.mf.common.runtime.IContainerState;
import com.sonicsw.mf.common.runtime.IState;
import com.sonicsw.mf.mgmtapi.runtime.IAgentManagerProxy;
import com.sonicsw.mf.mgmtapi.runtime.ProxyRuntimeException;
import com.sonicsw.mq.common.runtime.IConnectionData;
import com.sonicsw.mq.common.runtime.IConnectionMemberDetails;
import com.sonicsw.mq.common.runtime.IConnectionMemberInfo;
import com.sonicsw.mq.common.runtime.IConnectionTreeNode;
import com.sonicsw.mq.common.runtime.IQueueData;
import com.sonicsw.mq.common.runtime.ISubscriberData;
import com.sonicsw.mq.mgmtapi.config.IQueuesBean.IQueueAttributes;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;

import static com.digia.monitoring.sonicmq.util.SonicUtil.*;
import static com.digia.monitoring.sonicmq.util.DigestUtil.*;

/**
 * <p>Provides methods for SonicMQ component discovery and data collection.</p>
 * 
 * <p>This class supports both one time and iterative collecting of data into output model object
 * ({@link SonicMQMonitoringData}).</p>
 * 
 * <p>Discovery methods in this class only update the monitor's internal state. Collect methods are
 * called after discovery to gather data into {@link SonicMQMonitoringData} output model.</p>
 * 
 * @author Sami Pajunen
 */
public class SonicMQMonitor implements Closeable {
    
    /** Attribute name for connection member's state. */
    private static final String CONNECTION_MEMBER_STATE = "connection.member.State";
    /** Attribute name for connection member's type. */
    private static final String CONNECTION_MEMBER_TYPE = "connection.member.Type";
    /** Attribute name for connection member's MQ destination name. */
    private static final String CONNECTION_MEMBER_DESTINATION = "connection.member.Destination";
    
    private static final String[] DEFAULT_COLLECTORS = {
            "com.digia.monitoring.sonicmq.collector.AgentCollector",
            "com.digia.monitoring.sonicmq.collector.AgentManagerCollector",
            "com.digia.monitoring.sonicmq.collector.BrokerCollector",
            "com.digia.monitoring.sonicmq.collector.TopicSubscriptionCollector"
    };
    
    private static final String QUEUE_CONFIG_MAX_SIZE = "queue.config.QueueMaxSize";

    private Logger logger = LoggerFactory.getLogger(SonicMQMonitor.class);
    
    private ClientProxyFactory clientProxyFactory;
    
    private SonicMQMonitorConfiguration config;
    
    private List<SonicMQComponent> discoveredComponents = Collections.emptyList();
    private List<SonicMQConnection> discoveredConnections = Collections.emptyList();
    private List<SonicMQQueue> discoveredQueues = Collections.emptyList();
    private List<SonicMQSubscriber> discoveredSubscribers = Collections.emptyList();
    
    private List<ICollector> collectors;
    
    /**
     * Creates new SonicMQMonitor with given monitor configuration.
     * @param config Monitor configuration
     * @throws SonicMQMonitoringException Thrown if creating monitor instance fails
     */
    public SonicMQMonitor(SonicMQMonitorConfiguration config) throws SonicMQMonitoringException {
        clientProxyFactory = new ClientProxyFactory(config.getDomain(), config.getLocation(), config.getUsername(), config.getPassword(), config.getTimeout());
        collectors = createCollectors(config.getCollectors());
        this.config = config;
    }
    
    /**
     * Discovers and collects components into this class's internal state. This should be called prior to collecting 
     * monitoring data using other methods. It can also be called to refresh discovery data before new data collection.
     */
    public void discoverComponents() {
        List<SonicMQComponent> discoveredComponents = new ArrayList<SonicMQComponent>();
        IAgentManagerProxy domainProxy = clientProxyFactory.getDomainLevelAgentManagerProxy();
        for (IState state : domainProxy.getCollectiveState()) {
            if (state instanceof IContainerState) {
                for (IComponentState componentState : ((IContainerState) state).getComponentStates()) {
                    SonicMQComponent component = new SonicMQComponent(componentState);
                    // Add only known component types in discovered list
                    if (component.getType() != null) {
                        discoveredComponents.add(component);
                    }
                }
            }
        }
        this.discoveredComponents = Collections.unmodifiableList(discoveredComponents);
    }
    
    /**
     * Discovers connections and subscribers for previously discovered components. Requires that
     * {@link #discoverComponents()} has been called at least once.
     */
    public void discoverConnectionsAndSubscribers() {
        List<SonicMQConnection> discoveredConnections = new ArrayList<SonicMQConnection>();
        List<SonicMQSubscriber> discoveredSubscribers = new ArrayList<SonicMQSubscriber>();
        for (SonicMQComponent component : discoveredComponents) {
            if (component.getType() == ComponentType.BROKER && component.isOnline()) {
                IBrokerProxy proxy = clientProxyFactory.getBrokerProxy(component.getJmxName());
                for (IConnectionData conn : getAllConnections(proxy)) {
                    if (config.isCollectAllConnections() || conn.isApplicationConnection()) {
                        SonicMQConnection connection = new SonicMQConnection(component, conn);
                        discoveredConnections.add(connection);
                        for (ISubscriberData subscriberData : getSubscribers(proxy, conn)) {
                            SonicMQSubscriber subscriber = new SonicMQSubscriber(component, conn, subscriberData);
                            discoveredSubscribers.add(subscriber);
                        }
                    }
                }
            }
        }
        this.discoveredConnections = Collections.unmodifiableList(discoveredConnections);
        this.discoveredSubscribers = Collections.unmodifiableList(discoveredSubscribers);
    }

    /**
     * Discovers queues for previously discovered components. Requires that {@link #discoverComponents()}
     * has been called at least once.
     */
    public void discoverQueues() {
        List<SonicMQQueue> discoveredQueues = new ArrayList<SonicMQQueue>();
        for (SonicMQComponent component : discoveredComponents) {
            if (component.getType() == ComponentType.BROKER && component.isOnline()) {
                IBrokerProxy proxy = clientProxyFactory.getBrokerProxy(component.getJmxName());
                for (IQueueData queueData : getAllQueues(proxy)) {
                    if (!queueData.isTemporaryQueue()) {
                        SonicMQQueue queue = new SonicMQQueue(component, queueData);
                        discoveredQueues.add(queue);
                    }
                }
            }
        }
        this.discoveredQueues = Collections.unmodifiableList(discoveredQueues);
    }
    
    /**
     * Collects data for previously discovered components into provided monitoring data.
     * @param data Monitoring data
     */
    public void collectMetricsData(SonicMQMonitoringData data) {
        for (SonicMQComponent component : discoveredComponents) {
            try {
                for (ICollector collector : collectors) {
                    collector.collectData(clientProxyFactory, component, data);
                }
            } catch (ProxyRuntimeException ex) {
                logger.error("Unable to collect data from component " + component.getName() + ".", ex);
            }
        }
    }
    
    public void collectConfigurationData(SonicMQMonitoringData data) {
        for (SonicMQQueue queue : discoveredQueues) {
            try {
                IQueueAttributes queueAttributes = 
                    clientProxyFactory.getQueueAttributes(queue.getBroker().getName(), queue.getName());
                DiscoveryItemData brokerData = data.getItemData(DiscoveryItemClass.Broker, queue.getBroker().getName());
                DiscoveryItemData queueData = brokerData.getItemData(DiscoveryItemClass.Queue, sha1hex(queue.getName()));
                
                queueData.setData(QUEUE_CONFIG_MAX_SIZE, queueAttributes.getQueueMaxSize());
            } catch (MgmtException ex) {
                logger.warn("Could not retrieve configuration for queue " + queue.getName() + ".", ex);
            }
        }
    }
    
    /**
     * Collects previously discovered components into provided monitoring data.
     * @param data Monitoring data
     */
    public void collectDiscoveryItems(SonicMQMonitoringData data) {
        for (ISonicMQComponent component : discoveredComponents) {
            data.addDiscoveryItem(component.getType().getDiscoveryItemClass(),
                    new NamedDiscoveryItem(component.getName()));
        }
        for (SonicMQConnection connection : discoveredConnections) {
            if (connection.getId() != null) {
                data.addDiscoveryItem(DiscoveryItemClass.Connection,
                        new ConnectionDiscoveryItem(connection.getBroker().getName(), connection.getId(),
                                connection.getName(), connection.getHost(), connection.getUser()));
            } else {
                logger.warn("Cannot collect connection for user {} from {} due to missing identifier.",
                        connection.getUser(), connection.getHost());
            }
        }
        for (SonicMQQueue queue : discoveredQueues) {
            data.addDiscoveryItem(DiscoveryItemClass.Queue,
                    new QueueDiscoveryItem(queue.getBroker().getName(), queue.getName(), queue.isClustered()));
        }
        for (SonicMQSubscriber subscriber : discoveredSubscribers) {
            data.addDiscoveryItem(DiscoveryItemClass.TopicSubscription,
                    new TopicSubscriptionDiscoveryItem(subscriber.getBroker(), subscriber.getConnectionId(),
                            subscriber.getTopicId(), subscriber.getTopic(), subscriber.getHost(),
                            subscriber.getUser()));
        }
    }

    /**
     * Collects connection data for previously discovered brokers into provided monitoring data.
     * @param data Monitoring data
     */
    public void collectConnectionData(SonicMQMonitoringData data) {
        for (ISonicMQComponent component : discoveredComponents) {
            if (component.getType() == ComponentType.BROKER) {
                IBrokerProxy proxy = clientProxyFactory.getBrokerProxy(component.getJmxName());
                for (IConnectionData conn : getAllConnections(proxy)) {
                    if (config.isCollectAllConnections() || conn.isApplicationConnection()) {
                        long ref = conn.getConnectionMemberRef();
                        data.addConnectionData(
                                component.getName(),
                                conn.getHost(), 
                                conn.getUser(),
                                collectSubscriberData(proxy, ref),
                                collectConnectionMemberData(proxy, getConnectionTree(proxy, ref)));
                    }
                }
            }
        }
    }
    
    /**
     * Closes underlying SonicMQ connection.
     */
    public void close() {
        clientProxyFactory.close();
    }

    /**
     * Returns the target domain of this monitor.
     * @return Domain
     */
    public String getDomain() {
        return config.getDomain();
    }
    
    /**
     * Collects subscriber data for given connection reference.
     * @param proxy Broker proxy instance
     * @param memberRef Connection member reference
     * @return Collected subscriber data
     */
    private List<SubscriberData> collectSubscriberData(IBrokerProxy proxy, long memberRef) {
    	long start = System.currentTimeMillis();
        List<SubscriberData> subscriberDataList = new ArrayList<SubscriberData>();
        for (Object s : proxy.getSubscribers(memberRef)) {
            if (s instanceof ISubscriberData) {
                ISubscriberData subscriber = (ISubscriberData) s;
                SubscriberData subscriberData = new SubscriberData(
                        subscriber.getTopicName(),
                        subscriber.getSubscriptionName(),
                        subscriber.getMessageCount(),
                        subscriber.getMessageSize());
                subscriberDataList.add(subscriberData);
            }
        }
    	long end = System.currentTimeMillis();
    	logger.debug("Collecting subscriber data took {}ms", (end-start));
        return subscriberDataList;
    }

    /**
     * Recursively collects connection member data for given list of connection member references.
     * @param proxy Broker proxy instance
     * @param memberRefs Member refs
     * @return List of connection members' collected data
     */
    private List<ConnectionMemberData> collectConnectionMemberData(IBrokerProxy proxy, IConnectionTreeNode node) {
        List<ConnectionMemberData> memberDataList = new ArrayList<ConnectionMemberData>();
        for (IConnectionTreeNode childNode : getConnectionTreeNodeChildren(node)) {
            IConnectionMemberInfo memberInfo = childNode.getInfo();
            IConnectionMemberDetails memberDetails = proxy.getConnectionMemberDetails(memberInfo.getRef());
            
            if (memberDetails != null) {
                ConnectionMemberData memberData = 
                        new ConnectionMemberData(collectConnectionMemberData(proxy, childNode));
                
                memberData.addAttribute(CONNECTION_MEMBER_STATE, memberDetails.getStateString());
                memberData.addAttribute(CONNECTION_MEMBER_TYPE, memberInfo.getTypeString());
                
                String destinationName = getDestinationName(memberDetails);
                if (destinationName != null) {
                    memberData.addAttribute(CONNECTION_MEMBER_DESTINATION, destinationName);
                }
                memberDataList.add(memberData);
            }
        }
        return memberDataList;
    }
    
    private List<ICollector> createCollectors(List<String> collectorNames) throws SonicMQMonitoringException {
        if (collectorNames == null || collectorNames.isEmpty()) {
            collectorNames = Arrays.asList(DEFAULT_COLLECTORS);
        }
        List<ICollector> collectors = new ArrayList<ICollector>();
        for (String collectorName : collectorNames) {
            collectors.add(CollectorFactory.createCollector(collectorName));
        }
        return collectors;
    }

}

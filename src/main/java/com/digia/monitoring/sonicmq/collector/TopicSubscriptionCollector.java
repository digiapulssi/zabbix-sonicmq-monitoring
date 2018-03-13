package com.digia.monitoring.sonicmq.collector;

import com.digia.monitoring.sonicmq.DiscoveryItemClass;
import com.digia.monitoring.sonicmq.model.DiscoveryItemData;
import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;
import com.sonicsw.mq.common.runtime.IConnectionData;
import com.sonicsw.mq.common.runtime.ISubscriberData;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;

import static com.digia.monitoring.sonicmq.util.SonicUtil.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Collector implementation for collecting topic subscription information.</p>
 * 
 * <p>Collects largest message count and size for connection and topic name.</p>
 * 
 * @author Sami Pajunen
 */
public class TopicSubscriptionCollector extends CollectorBase {

    private Logger logger = LoggerFactory.getLogger(TopicSubscriptionCollector.class);
    
    private static final String TOPICSUBSCRIPTION_MESSAGECOUNT = "topicsubscription.MessageCount";
    private static final String TOPICSUBSCRIPTION_MESSAGESIZE = "topicsubscription.MessageSize";

    @Override
    protected void collectBrokerData(IBrokerProxy proxy, String name, SonicMQMonitoringData data) {
        DiscoveryItemData brokerData = data.getItemData(DiscoveryItemClass.Broker, name);

        for (IConnectionData connection : getAllConnections(proxy)) {
            String connectionKey = getIdentifier(connection);
            if (connectionKey != null) {
                DiscoveryItemData connectionData = brokerData.getItemData(DiscoveryItemClass.Connection, connectionKey);
                for (ISubscriberData subscriber : getSubscribers(proxy, connection)) {
                    DiscoveryItemData subscriberData = connectionData.getItemData(DiscoveryItemClass.TopicSubscription,
                            getIdentifier(subscriber));
                    Long messageCount = (Long) subscriberData.getData(TOPICSUBSCRIPTION_MESSAGECOUNT);
                    if (messageCount == null || messageCount < subscriber.getMessageCount()) {
                        subscriberData.setData(TOPICSUBSCRIPTION_MESSAGECOUNT, subscriber.getMessageCount());
                        subscriberData.setData(TOPICSUBSCRIPTION_MESSAGESIZE, subscriber.getMessageSize());
                    }
                }
            } else {
                logger.warn("Cannot collect topic subscription information due to missing connection ID. User={}, Host={}",
                        connection.getUser(), connection.getHost());
            }
        }
    }
}

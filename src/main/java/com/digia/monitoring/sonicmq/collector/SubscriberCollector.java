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
 * Collector implementation for collecting connection/subscriber information.
 * @author Sami Pajunen
 */
public class SubscriberCollector extends CollectorBase {

    private Logger logger = LoggerFactory.getLogger(SubscriberCollector.class);

    @Override
    protected void collectBrokerData(IBrokerProxy proxy, String name, SonicMQMonitoringData data) {
        DiscoveryItemData brokerData = data.getItemData(DiscoveryItemClass.Broker, name);

        for (IConnectionData connection : getAllConnections(proxy)) {
            String connectionKey = getIdentifier(connection);
            if (connectionKey != null) {
                DiscoveryItemData connectionData = brokerData.getItemData(DiscoveryItemClass.Connection, connectionKey);
                for (ISubscriberData subscriber : getSubscribers(proxy, connection)) {
                    DiscoveryItemData subscriberData = connectionData.getItemData(DiscoveryItemClass.Subscriber,
                            getIdentifier(subscriber));
                    subscriberData.setData("subscriber.TopicName", subscriber.getTopicName());
                    subscriberData.setData("subscriber.SubscriptionName", subscriber.getSubscriptionName());
                    subscriberData.setData("subscriber.MessageCount", subscriber.getMessageCount());
                    subscriberData.setData("subscriber.MessageSize", subscriber.getMessageSize());
                    subscriberData.setData("subscriber.IsDurable", subscriber.isDurable());
                    subscriberData.setData("subscriber.IsConnectionConsumer", subscriber.isConnectionConsumer());
                }
            } else {
                logger.warn("Cannot collect connection subscribers due to missing connection ID. User={}, Host={}",
                        connection.getUser(), connection.getHost());
            }
        }
    }
}

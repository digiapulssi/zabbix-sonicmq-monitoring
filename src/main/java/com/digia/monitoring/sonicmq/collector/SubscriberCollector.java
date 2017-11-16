package com.digia.monitoring.sonicmq.collector;

import com.digia.monitoring.sonicmq.DiscoveryItemClass;
import com.digia.monitoring.sonicmq.model.DiscoveryItemData;
import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;
import com.sonicsw.mq.common.runtime.IConnectionData;
import com.sonicsw.mq.common.runtime.ISubscriberData;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;

import static com.digia.monitoring.sonicmq.util.DigestUtil.*;
import static com.digia.monitoring.sonicmq.util.SonicUtil.*;

/**
 * Collector implementation for collecting connection/subscriber information.
 * @author Sami Pajunen
 */
public class SubscriberCollector extends CollectorBase {
	
	@Override
	protected void collectBrokerData(IBrokerProxy proxy, String name, SonicMQMonitoringData data) {
        DiscoveryItemData brokerData = data.getItemData(DiscoveryItemClass.Broker, name);
        
        for (IConnectionData connection : getAllConnections(proxy)) {
        	String connectionKey = sha1hex(connection.getConnectID());
        	DiscoveryItemData connectionData = brokerData.getItemData(DiscoveryItemClass.Connection, connectionKey);
        	for (ISubscriberData subscriber : getSubscribers(proxy, connection)) {
        		DiscoveryItemData subscriberData = 
        				connectionData.getItemData(DiscoveryItemClass.Subscriber, getItemName(subscriber));
        		subscriberData.setValue("subscriber.TopicName", subscriber.getTopicName());
        		subscriberData.setValue("subscriber.SubscriptionName", subscriber.getSubscriptionName());
        		subscriberData.setValue("subscriber.MessageCount", subscriber.getMessageCount());
        		subscriberData.setValue("subscriber.MessageSize", subscriber.getMessageSize());
        		subscriberData.setValue("subscriber.IsDurable", subscriber.isDurable());
        		subscriberData.setValue("subscriber.IsConnectionConsumer", subscriber.isConnectionConsumer());
        	}
        }
	}
	
	/**
	 * Returns item name for subscriber.
	 * @param subscriberData Subscriber
	 * @return
	 */
	private String getItemName(ISubscriberData subscriberData) {
		return sha1hex(subscriberData.getTopicName());
	}
}

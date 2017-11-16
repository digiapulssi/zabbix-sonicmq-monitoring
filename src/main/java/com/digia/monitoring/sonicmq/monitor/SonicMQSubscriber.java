package com.digia.monitoring.sonicmq.monitor;

import com.sonicsw.mq.common.runtime.IConnectionData;
import com.sonicsw.mq.common.runtime.ISubscriberData;

/**
 * Internal model for discovered SonicMQ subscriber.
 * @author Sami Pajunen
 */
public class SonicMQSubscriber {
	/** Subscription's connection. */
	private String connectionName;
	
	/** Subscribed topic. */
	private String topic;
	
	/** Host. */
	private String host;
	
	/** User. */
	private String user;
	
	/**
	 * Creates new SonicMQSubscriber.
	 * @param connection Connection data of subscription
	 * @param subscriber Subscriber data of subscription
	 */
	public SonicMQSubscriber(IConnectionData connection, ISubscriberData subscriber) {
		this.connectionName = connection.getConnectID();
		this.topic = subscriber.getTopicName();
		this.host = connection.getHost();
		this.user = connection.getUser();
	}
	
	public String getHost() {
		return host;
	}
	
	public String getConnectionName() {
		return connectionName;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getTopic() {
		return topic;
	}
}

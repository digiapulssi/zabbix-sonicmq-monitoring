package com.digia.monitoring.sonicmq.monitor;

import com.sonicsw.mq.common.runtime.IConnectionData;
import com.sonicsw.mq.common.runtime.ISubscriberData;

import static com.digia.monitoring.sonicmq.util.SonicUtil.*;

/**
 * Internal model for discovered SonicMQ subscriber.
 * @author Sami Pajunen
 */
public class SonicMQSubscriber {
	/** Subscription's connection. */
	private String connectionId;
	
	/** Subscribed topic identifier. */
	private String topicId;
	
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
		this.connectionId = getIdentifier(connection);
		this.topicId = getIdentifier(subscriber);
		this.topic = subscriber.getTopicName();
		this.host = connection.getHost();
		this.user = connection.getUser();
	}
	
	public String getHost() {
		return host;
	}
	
	public String getConnectionId() {
		return connectionId;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public String getTopicId() {
		return topicId;
	}
}

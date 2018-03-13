package com.digia.monitoring.sonicmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.digia.monitoring.sonicmq.util.DigestUtil.*;

public class TopicSubscriptionDiscoveryItem extends NamedDiscoveryItem {
	
	@JsonProperty("{#HOST}")
	private String host;
	
	@JsonProperty("{#USER}")
	private String user;
	
	@JsonProperty("{#ID}")
	private String id;
	
	@JsonProperty("{#CONNECTIONID}")
	private String connectionId;
	
	@JsonProperty("{#BROKER}")
	private String broker;
	
	public TopicSubscriptionDiscoveryItem(String broker, String connectionId, String topicId, String topic, String host, String user) {
		super(topic);
		this.host = host;
		this.user = user;
		this.id = sha1hex(topic);
		this.connectionId = connectionId;
		this.broker = broker;
	}

}

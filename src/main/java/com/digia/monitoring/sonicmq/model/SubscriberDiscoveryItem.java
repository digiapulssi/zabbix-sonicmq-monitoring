package com.digia.monitoring.sonicmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.digia.monitoring.sonicmq.util.DigestUtil.*;

public class SubscriberDiscoveryItem extends NamedDiscoveryItem {
	
	@JsonProperty("{#HOST}")
	private String host;
	
	@JsonProperty("{#USER}")
	private String user;
	
	@JsonProperty("{#ID}")
	private String id;
	
	@JsonProperty("{#CONNECTIONID}")
	private String connectionId;
	
	public SubscriberDiscoveryItem(String connectionName, String topic, String host, String user) {
		super(topic);
		this.host = host;
		this.user = user;
		this.id = sha1hex(topic);
		this.connectionId = sha1hex(connectionName);
	}

}

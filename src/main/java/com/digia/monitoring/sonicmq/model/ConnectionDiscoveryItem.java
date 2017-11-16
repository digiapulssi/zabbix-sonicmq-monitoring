package com.digia.monitoring.sonicmq.model;

import static com.digia.monitoring.sonicmq.util.DigestUtil.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectionDiscoveryItem extends NamedDiscoveryItem {
	
	@JsonProperty("{#BROKER}")
	private String broker;
	
	@JsonProperty("{#HOST}")
	private String host;
	
	@JsonProperty("{#USER}")
	private String user;
	
	@JsonProperty("{#ID}")
	private String id;
	
	public ConnectionDiscoveryItem(String broker, String name, String host, String user) {
		super(name);
		this.broker = broker;
		this.host = host;
		this.user = user;
		this.id = sha1hex(name);
	}
}

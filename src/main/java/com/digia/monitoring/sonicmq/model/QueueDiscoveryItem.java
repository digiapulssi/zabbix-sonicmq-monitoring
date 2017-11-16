package com.digia.monitoring.sonicmq.model;

import static com.digia.monitoring.sonicmq.util.DigestUtil.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueueDiscoveryItem extends NamedDiscoveryItem {
	
	@JsonProperty("{#BROKER}")
	private String broker;
	
	@JsonProperty("{#ID}")
	private String id;

	public QueueDiscoveryItem(String broker, String name) {
		super(name);
		this.broker = broker;
		this.id = sha1hex(name);
	}
}

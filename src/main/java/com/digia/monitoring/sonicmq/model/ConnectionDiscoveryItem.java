package com.digia.monitoring.sonicmq.model;

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

    public ConnectionDiscoveryItem(String broker, String id, String name, String host, String user) {
        super(name);
        this.broker = broker;
        this.host = host;
        this.user = user;
        this.id = id;
    }

    public String getBroker() {
        return broker;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getId() {
        return id;
    }
}

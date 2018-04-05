package com.digia.monitoring.sonicmq.model;

import static com.digia.monitoring.sonicmq.util.DigestUtil.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueueDiscoveryItem extends NamedDiscoveryItem {

    @JsonProperty("{#BROKER}")
    private String broker;

    @JsonProperty("{#ID}")
    private String id;
    
    @JsonProperty("{#CLUSTERED}")
    private boolean clustered;

    public QueueDiscoveryItem(String broker, String name, boolean clustered) {
        super(name);
        this.broker = broker;
        this.id = sha1hex(name);
        this.clustered = clustered;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj.getClass() == getClass()) {
                String broker = ((QueueDiscoveryItem) obj).broker;
                return this.broker != null ? this.broker.equals(broker) : broker == null;
            }
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        return broker != null ? hashCode + broker.hashCode() : hashCode;
    }
}

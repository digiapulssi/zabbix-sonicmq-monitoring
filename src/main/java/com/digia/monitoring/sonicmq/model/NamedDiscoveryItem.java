package com.digia.monitoring.sonicmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Zabbix compatible discovery item model defining just discovered item name.
 * @author Sami Pajunen
 */
public class NamedDiscoveryItem {
    /** Item name. */
    private String name;
    
    public NamedDiscoveryItem(String name) {
        this.name = name;
    }
    
    @JsonProperty("{#NAME}")
    public String getName() {
        return name;
    }
}
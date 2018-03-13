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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj.getClass() == getClass()) {
            String name = ((NamedDiscoveryItem) obj).name;
            return this.name != null ? this.name.equals(name) : name == null;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
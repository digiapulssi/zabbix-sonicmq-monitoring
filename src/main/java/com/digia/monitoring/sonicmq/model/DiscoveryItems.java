package com.digia.monitoring.sonicmq.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic Zabbix compatible discovery result model.
 * @author Sami Pajunen
 */
public class DiscoveryItems<T> {
    /** List of items discovered. */
    private Set<T> data = new HashSet<T>();
    
    public void add(T item) {
        data.add(item);
    }
    
    public Set<T> getData() {
        return Collections.unmodifiableSet(data);
    }
}
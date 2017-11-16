package com.digia.monitoring.sonicmq.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic Zabbix compatible discovery result model.
 * @author Sami Pajunen
 */
public class DiscoveryItems<T> {
    /** List of items discovered. */
    private List<T> data = new ArrayList<T>();
    
    public void add(T item) {
        data.add(item);
    }
    
    public List<T> getData() {
        return Collections.unmodifiableList(data);
    }
}
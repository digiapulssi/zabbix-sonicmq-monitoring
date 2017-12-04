package com.digia.monitoring.sonicmq.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.digia.monitoring.sonicmq.DiscoveryItemClass;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * <p>Monitoring data model for SonicMQ.</p>
 * 
 * <p>Provides only write methods because it is meant only to combine data fragments for JSON serialization.</p>
 * 
 * @author Sami Pajunen
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY)
public class SonicMQMonitoringData {
    /** Collected domain. */
    private String domain;
    /** Discovered components. */
    @JsonInclude(value=Include.NON_NULL)
    private Map<DiscoveryItemClass,DiscoveryItems<?>> discovery;
    /** Data for discovered components. */
    @JsonInclude(value=Include.NON_NULL)
    private DiscoveryItemDataMap data;
    /** Connection data for broker. */
    @JsonInclude(value=Include.NON_NULL)
    private Map<String,List<ConnectionData>> connectionData;
    
    /**
     * Creates new SonicMQMonitoringData.
     * @param domain Collected domain
     */
    public SonicMQMonitoringData(String domain) {
        this.domain = domain;
    }

    /**
     * Returns collected domain.
     * @return Domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Adds new discovery item.
     * @param itemClass Discovery item type
     * @param item Discovery item
     * @param <T> Type of discovery item
     */
    public <T> void addDiscoveryItem(DiscoveryItemClass itemClass, T item) {
        if (discovery == null) {
            discovery = new LinkedHashMap<DiscoveryItemClass,DiscoveryItems<?>>();
        }
        @SuppressWarnings("unchecked")
        DiscoveryItems<T> discoveryItems = (DiscoveryItems<T>) discovery.get(itemClass);
        if (discoveryItems == null) {
            discoveryItems = new DiscoveryItems<T>();
            discovery.put(itemClass, discoveryItems);
        }
        discoveryItems.add(item);
    }
    
    /**
     * Adds new key-value data for discovered item.
     * @param itemClass Discovery item type
     * @param discoveryItemName Discovery item name
     * @param key Collected value key
     * @param value Collected value
     */
    public void addData(DiscoveryItemClass itemClass, String discoveryItemName, String key, Object value) {
        if (data == null) {
            data = new DiscoveryItemDataMap();
        }
        DiscoveryItemData itemData = data.getData(itemClass, discoveryItemName);
        itemData.setData(key, value);
    }
    
    public DiscoveryItemData getItemData(DiscoveryItemClass itemClass, String discoveryItemName) {
        if (data == null) {
            data = new DiscoveryItemDataMap();
        }
    	return data.getData(itemClass, discoveryItemName);
    }
    
    /**
     * Adds connection data for given broker component name. Overwrites if data already exists for same broker name.
     * @param componentName Broker component name
     * @param host Connection host
     * @param user Connection user
     * @param subscribers Subscribers
     * @param members Connection members
     */
    public void addConnectionData(String componentName, String host, String user, List<SubscriberData> subscribers, List<ConnectionMemberData> members) {
        if (connectionData == null) {
            connectionData = new LinkedHashMap<String, List<ConnectionData>>();
        }
        List<ConnectionData> connectionDataList = connectionData.get(componentName);
        if (connectionDataList == null) {
            connectionDataList = new ArrayList<ConnectionData>();
            connectionData.put(componentName, connectionDataList);
        }
        connectionDataList.add(new ConnectionData(host, user, subscribers, members));
    }
    
    public ConnectionDiscoveryItem getDiscoveredConnection(String id) {
        @SuppressWarnings("unchecked")
        DiscoveryItems<ConnectionDiscoveryItem> connections = (DiscoveryItems<ConnectionDiscoveryItem>) discovery.get(DiscoveryItemClass.Connection);
        if (connections != null) {
            for (ConnectionDiscoveryItem item : connections.getData()) {
                if (item.getId().equals(id)) {
                    return item;
                }
            }
        }
        return null;
    }
    
    /**
     * Serializes the data to JSON.
     * @param out Output writer
     * @param pretty True to generate "pretty" output (with linefeeds)
     * @throws IOException Thrown if writing fails
     */
    public void toJson(Writer out, boolean pretty) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer;
            if (pretty) {
                writer = mapper.writerWithDefaultPrettyPrinter();
            } else {
                writer = mapper.writer();
            }
            writer.writeValue(out, this);
        } catch (JsonGenerationException ex) {
            throw new IOException("Unable to serialize JSON data.", ex);
        } catch (JsonMappingException ex) {
            throw new IOException("Unable to serialize JSON data.", ex);
        }
    }
}

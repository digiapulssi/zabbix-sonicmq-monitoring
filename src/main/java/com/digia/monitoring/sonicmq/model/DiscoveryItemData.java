package com.digia.monitoring.sonicmq.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.digia.monitoring.sonicmq.DiscoveryItemClass;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class DiscoveryItemData {
	
	private DiscoveryItemDataMap items;
	private Map<String,Object> data = new LinkedHashMap<String,Object>();
	
	public void setValue(String key, Object value) {
		data.put(key, value);
	}
	
    @JsonInclude(value=Include.NON_NULL)
	public Map<String, Object> getData() {
		return Collections.unmodifiableMap(data);
	}
	
    @JsonInclude(value=Include.NON_NULL)
	public DiscoveryItemDataMap getItems() {
		return items;
	}
	
	public DiscoveryItemData getItemData(DiscoveryItemClass itemClass, String discoveryItemName) {
		if (items == null) {
			items = new DiscoveryItemDataMap();
		}
		Map<String,DiscoveryItemData> classMap = items.get(itemClass);
		if (classMap == null) {
			classMap = new LinkedHashMap<String,DiscoveryItemData>();
			items.put(itemClass, classMap);
		}
		DiscoveryItemData data = classMap.get(discoveryItemName);
		if (data == null) {
			data = new DiscoveryItemData();
			classMap.put(discoveryItemName, data);
		}
		return data;
	}
}

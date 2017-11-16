package com.digia.monitoring.sonicmq.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.digia.monitoring.sonicmq.DiscoveryItemClass;

/**
 * Model for discovery item data map.
 * @author Sami Pajunen
 */
public class DiscoveryItemDataMap extends LinkedHashMap<DiscoveryItemClass, Map<String,DiscoveryItemData>> {
	private static final long serialVersionUID = 2558858254059645511L;

	/**
	 * Gets discovery item data for item class and discovery item name. If one does not yet exist it is created and returned.
	 * @param itemClass Item class
	 * @param discoveryItemName Item name
	 * @return Discovery item data
	 */
	public DiscoveryItemData getData(DiscoveryItemClass itemClass, String discoveryItemName) {
		Map<String,DiscoveryItemData> classMap = get(itemClass);
		if (classMap == null) {
			classMap = new LinkedHashMap<String,DiscoveryItemData>();
			put(itemClass, classMap);
		}
		DiscoveryItemData data = classMap.get(discoveryItemName);
		if (data == null) {
			data = new DiscoveryItemData();
			classMap.put(discoveryItemName, data);
		}
		return data;
	}
}

package com.digia.monitoring.sonicmq.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model for collected SonicMQ connection member data.
 * @author Sami Pajunen
 */
public class ConnectionMemberData {
    /** Child members. */
    private List<ConnectionMemberData> members;
    
    /** Collected member attributes. */
    private Map<String,Object> attributes = new HashMap<String, Object>();
    
    /**
     * Creates new ConnectionMemberData without child members.
     */
    public ConnectionMemberData() {
        members = Collections.emptyList();
    }
    
    /**
     * Creates new ConnectionMemberData with child members.
     * @param members Child members
     */
    public ConnectionMemberData(List<ConnectionMemberData> members) {
        this.members = members;
    }
    
    /**
     * Returns unmodifiable child members.
     * @return Child members
     */
    public List<ConnectionMemberData> getMembers() {
        return Collections.unmodifiableList(members);
    }
    
    /**
     * Returns unmodifiable member attributes.
     * @return Member attributes
     */
    public Map<String,Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
    
    /**
     * Adds/overwrites member attribute.
     * @param key Attribute key
     * @param value Attribute value
     */
    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
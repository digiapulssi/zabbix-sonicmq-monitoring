package com.digia.monitoring.sonicmq;

import com.sonicsw.mf.common.runtime.IIdentity;

/**
 * Enumeration of collected SonicMQ component types.
 * @author Sami Pajunen
 */
public enum ComponentType {
    /** Broker component. */
    BROKER("MQ_BROKER", DiscoveryItemClass.Broker), 
    /** Agent (container) component. */
    AGENT("MF_CONTAINER", DiscoveryItemClass.Agent), 
    /** Agent manager component. */
    AGENT_MANAGER("MF_AGENT_MANAGER", DiscoveryItemClass.AgentManager);
    
    /** Corresponding SonicMQ component type string. */
    private String type;
    /** Discovery item class for component. */
    private DiscoveryItemClass discoveryItemClass;
    
    /**
     * Creates new ComponentType.
     * @param type SonicMQ component type string
     * @param discoveryItemClass Discovery item class
     */
    private ComponentType(String type, DiscoveryItemClass discoveryItemClass) {
        this.type = type;
        this.discoveryItemClass = discoveryItemClass;
    }
    
    /**
     * Returns the matching discovery item class for this component.
     * @return Discovery item class
     */
    public DiscoveryItemClass getDiscoveryItemClass() {
        return discoveryItemClass;
    }

    /**
     * Resolves component type from SonicMQ identity object.
     * @param identity Identity object
     * @return Resolved component type or null if none matched.
     */
    public static ComponentType fromIdentity(IIdentity identity) {
        if (identity.getConfigIdentity() != null) {
            String type = identity.getConfigIdentity().getType();
            for (ComponentType value : ComponentType.values()) {
                if (value.type.equals(type)) {
                    return value;
                }
            }
        }
        return null;
    }
}
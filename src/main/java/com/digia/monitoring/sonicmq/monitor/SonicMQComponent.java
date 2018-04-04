package com.digia.monitoring.sonicmq.monitor;

import com.digia.monitoring.sonicmq.ComponentType;
import com.digia.monitoring.sonicmq.ISonicMQComponent;
import com.sonicsw.mf.common.runtime.IComponentIdentity;
import com.sonicsw.mf.common.runtime.IComponentState;
import com.sonicsw.mf.common.runtime.IContainerState;
import com.sonicsw.mf.common.runtime.IIdentity;
import com.sonicsw.mf.common.runtime.IState;

/**
 * Internal model for discovered SonicMQ component.
 * @author Sami Pajunen
 */
public class SonicMQComponent implements ISonicMQComponent {
    /** Canonical/JMX name of component. */
    private String jmxName;
    
    /** Component type. */
    private ComponentType type;

    /** Component name. */
    private String name;
    
    /** Name of container containing the component. */
    private String containerName;
    
    /** True if component is online. */
    private boolean online;
    
    /** Component's last discovered state name. */
    private String stateName;
    
    /** Component's last discovered state. */
    private short state;

    /**
     * Creates new SonicMQComponent.
     * @param state SonicMQ state object model.
     */
    public SonicMQComponent(IState state) {
        IIdentity identity = state.getRuntimeIdentity();
        jmxName = identity.getCanonicalName();
        type = ComponentType.fromIdentity(identity);
        containerName = identity.getContainerName();
        online = resolveComponentState(state);
        this.stateName = state.getStateString();
        this.state = state.getState();
        if (type == ComponentType.BROKER) {
            name = ((IComponentIdentity) identity).getComponentName();
        } else {
            name = containerName;
        }
    }
    
    private boolean resolveComponentState(IState state) {
    	if (state instanceof IComponentState && state.getState() == IComponentState.STATE_ONLINE) {
    		return true;
    	}
    	if (state instanceof IContainerState && state.getState() == IContainerState.STATE_ONLINE) {
    		return true;
    	}
    	return false;
    }
    
    public String getJmxName() {
        return jmxName;
    }
    
    public ComponentType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public String getContainerName() {
        return containerName;
    }
    
    public String getStateName() {
		return stateName;
	}
    
    public short getState() {
        return state;
    }
    
    public boolean isOnline() {
		return online;
	}
}
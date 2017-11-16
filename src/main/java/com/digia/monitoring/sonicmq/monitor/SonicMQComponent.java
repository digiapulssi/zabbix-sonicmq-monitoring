package com.digia.monitoring.sonicmq.monitor;

import com.digia.monitoring.sonicmq.ComponentType;
import com.digia.monitoring.sonicmq.ISonicMQComponent;
import com.sonicsw.mf.common.runtime.IComponentIdentity;
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

    /**
     * Creates new SonicMQComponent.
     * @param state SonicMQ state object model.
     */
    public SonicMQComponent(IState state) {
        IIdentity identity = state.getRuntimeIdentity();
        jmxName = identity.getCanonicalName();
        type = ComponentType.fromIdentity(identity);
        containerName = identity.getContainerName();
        if (type == ComponentType.BROKER) {
            name = ((IComponentIdentity) identity).getComponentName();
        } else {
            name = containerName;
        }
    }
    
    /* (non-Javadoc)
     * @see com.digia.monitoring.sonicmq.monitor.ISonicMQComponent#getJmxName()
     */
    @Override
    public String getJmxName() {
        return jmxName;
    }
    
    /* (non-Javadoc)
     * @see com.digia.monitoring.sonicmq.monitor.ISonicMQComponent#getType()
     */
    @Override
    public ComponentType getType() {
        return type;
    }
    
    /* (non-Javadoc)
     * @see com.digia.monitoring.sonicmq.monitor.ISonicMQComponent#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    
    /* (non-Javadoc)
     * @see com.digia.monitoring.sonicmq.monitor.ISonicMQComponent#getContainerName()
     */
    @Override
    public String getContainerName() {
        return containerName;
    }
}
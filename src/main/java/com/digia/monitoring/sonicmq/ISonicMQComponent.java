package com.digia.monitoring.sonicmq;

/**
 * Provides description model of SonicMQ component.
 * @author Sami Pajunen
 */
public interface ISonicMQComponent {

    /**
     * Returns canonical (JMX) name of component.
     * @return Canonical name
     */
    String getJmxName();

    /**
     * Returns type of component.
     * @return Component type
     */
    ComponentType getType();

    /**
     * Returns name of component.
     * @return Component name
     */
    String getName();

    /**
     * Returns name of component's container.
     * @return Container name
     */
    String getContainerName();
    
    /**
     * Returns name of component state.
     * @return Component state name
     */
    String getStateName();
    
    /**
     * Returns raw component state value.
     * @return Raw component state
     */
    short getState();
    
    /**
     * Returns true if component is online.
     * @return True if component is online
     */
    boolean isOnline();
}
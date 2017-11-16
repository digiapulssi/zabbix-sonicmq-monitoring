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

}
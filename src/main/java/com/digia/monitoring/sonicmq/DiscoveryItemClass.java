package com.digia.monitoring.sonicmq;

/**
 * Enumeration of different discovery item types.
 * 
 * @author Sami Pajunen
 */
public enum DiscoveryItemClass {
    /** Agent component. */
    Agent,
    /** Agent manager component. */
    AgentManager, 
    /** Broker component. */
    Broker,
    /** Queue. */
    Queue,
    /** Connection. */
    Connection,
    /** Topic subscriber. */
    Subscriber
}
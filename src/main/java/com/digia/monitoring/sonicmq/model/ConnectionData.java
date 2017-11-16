package com.digia.monitoring.sonicmq.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model for collected SonicMQ connection data.
 * @author Sami Pajunen
 */
public class ConnectionData {
    /** Connection client host. */
    private String host;
    /** Connection client user. */
    private String user;
    /** Subscribers registered for connection.*/
    private List<SubscriberData> subscribers;
    /** Connection members. */
    private List<ConnectionMemberData> members;
    
    /**
     * Creates new ConnectionData
     * @param host Client host
     * @param user Client user
     * @param subscribers Subscribers
     * @param members Connection members
     */
    public ConnectionData(String host, String user, List<SubscriberData> subscribers, List<ConnectionMemberData> members) {
        this.host = host;
        this.user = user;
        this.subscribers = subscribers;
        this.members = members;
    }

    @JsonProperty("connection.Host")
    public String getHost() {
        return host;
    }
    
    @JsonProperty("connection.User")
    public String getUser() {
        return user;
    }
    
    public List<ConnectionMemberData> getMembers() {
        return members;
    }
    
    public List<SubscriberData> getSubscribers() {
        return subscribers;
    }
}
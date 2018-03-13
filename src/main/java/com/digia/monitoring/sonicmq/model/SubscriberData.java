package com.digia.monitoring.sonicmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model for SonicMQ topic data.
 * @author Sami Pajunen
 */
public class SubscriberData {
    /** Subscribed topic. */
    private String topicName;
    /** Subscription name. */
    private String subscriptionName;
    /** Message count. */
    private long messageCount;
    /** Total messages size (bytes). */
    private long messageSize;
    
    /**
     * Creates new SubscriberData.
     * @param subscriptionName Subscription name
     * @param topicName Topic name
     * @param messageCount Message count
     * @param messageSize Total messages size
     */
    public SubscriberData(String topicName, String subscriptionName, long messageCount, long messageSize) {
        this.topicName = topicName;
        this.subscriptionName = subscriptionName;
        this.messageCount = messageCount;
        this.messageSize = messageSize;
    }
    
    @JsonProperty("connection.subscriber.TopicName")
    public String getTopicName() {
        return topicName;
    }
    
    @JsonProperty("connection.subscriber.SubscriptionName")
    public String getSubscriptionName() {
        return subscriptionName;
    }
    
    @JsonProperty("connection.subscriber.MessageCount")
    public long getMessageCount() {
        return messageCount;
    }
    
    @JsonProperty("connection.subscriber.MessageSize")
    public long getMessageSize() {
        return messageSize;
    }
}
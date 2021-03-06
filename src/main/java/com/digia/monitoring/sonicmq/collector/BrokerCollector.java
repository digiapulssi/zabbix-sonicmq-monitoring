package com.digia.monitoring.sonicmq.collector;

import static com.digia.monitoring.sonicmq.util.DigestUtil.*;

import com.digia.monitoring.sonicmq.DiscoveryItemClass;
import com.digia.monitoring.sonicmq.ISonicMQComponent;
import com.digia.monitoring.sonicmq.model.ConnectionDiscoveryItem;
import com.digia.monitoring.sonicmq.model.DiscoveryItemData;
import com.digia.monitoring.sonicmq.model.SonicMQMonitoringData;
import com.sonicsw.mf.common.metrics.IMetric;
import com.sonicsw.mf.common.metrics.IMetricIdentity;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;

/**
 * Collector implementation for broker.
 * @author Sami Pajunen
 */
public class BrokerCollector extends CollectorBase {

    /** List of metrics to check. */
    private static final IMetricIdentity[] metricIds = new IMetricIdentity[] {

            IBrokerProxy.BROKER_BYTES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.BROKER_BYTES_RECEIVEDPERSECOND_METRIC_ID,
            IBrokerProxy.BROKER_BYTES_TOPICDBSIZE_METRIC_ID,

            IBrokerProxy.BROKER_CONNECTIONS_COUNT_METRIC_ID,
            IBrokerProxy.BROKER_CONNECTIONS_REJECTEDPERMINUTE_METRIC_ID,

            IBrokerProxy.BROKER_MESSAGES_DELIVERED_METRIC_ID,
            IBrokerProxy.BROKER_MESSAGES_RECEIVED_METRIC_ID,
            IBrokerProxy.BROKER_MESSAGES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.BROKER_MESSAGES_RECEIVEDPERSECOND_METRIC_ID,

            IBrokerProxy.CONNECTION_BYTES_DELIVERED_METRIC_ID,
            IBrokerProxy.CONNECTION_BYTES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.CONNECTION_BYTES_RECEIVED_METRIC_ID,
            IBrokerProxy.CONNECTION_BYTES_RECEIVEDPERSECOND_METRIC_ID,

            IBrokerProxy.CONNECTION_MESSAGES_DELIVERED_METRIC_ID,
            IBrokerProxy.CONNECTION_MESSAGES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.CONNECTION_MESSAGES_RECEIVED_METRIC_ID,
            IBrokerProxy.CONNECTION_MESSAGES_RECEIVEDPERSECOND_METRIC_ID,

            IBrokerProxy.QUEUE_MESSAGES_COUNT_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_MAXDEPTH_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_RECEIVEDPERSECOND_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_SIZE_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_TIMEINQUEUE_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_MAXAGE_METRIC_ID
    };

    private static final String BROKER_STATE = "broker.State";
    private static final String BROKER_STATE_NAME = "broker.StateName";
    private static final String BROKER_REPLICATION_STATE = "broker.ReplicationState";
    private static final String BROKER_REPLICATION_TYPE = "broker.ReplicationType";

    private static final String CONNECTION_HOST = "connection.Host";
    private static final String CONNECTION_USER = "connection.User";
    
    public void collectBrokerData(IBrokerProxy proxy, ISonicMQComponent component, SonicMQMonitoringData data) {
        String name = component.getName();
        data.addData(DiscoveryItemClass.Broker, name, BROKER_STATE, String.valueOf(component.getState()));
        data.addData(DiscoveryItemClass.Broker, name, BROKER_STATE_NAME, component.getStateName());
        if (component.isOnline()) {
            DiscoveryItemData itemData = data.getItemData(DiscoveryItemClass.Broker, name);
    
            data.addData(DiscoveryItemClass.Broker, name, BROKER_REPLICATION_STATE, proxy.getReplicationStateString());
            data.addData(DiscoveryItemClass.Broker, name, BROKER_REPLICATION_TYPE, proxy.getReplicationType());
    
            IMetricIdentity[] activeMetrics = proxy.getActiveMetrics(metricIds);
            IMetric[] metrics = proxy.getMetricsData(activeMetrics, false).getMetrics();
            for (IMetric m : metrics) {
                MetricSpecifier specifier = new MetricSpecifier(m.getMetricIdentity(), name);
                // Add broker metrics directly and other metrics as categorized child of broker
                // metrics
                switch (specifier.getItemClass()) {
                case Broker:
                    itemData.setData(specifier.getMetric(), m.getValue());
                    break;
                case Connection:
                    DiscoveryItemData connectionData = itemData.getItemData(specifier.getItemClass(), specifier.getEntity());
                    populateConnectionData(connectionData, data.getDiscoveredConnection(specifier.getEntity()));
                    connectionData.setData(specifier.getMetric(), m.getValue());
                    break;
                case Queue:
                    DiscoveryItemData queueData = itemData.getItemData(specifier.getItemClass(), specifier.getEntity());
                    queueData.setData(specifier.getMetric(), m.getValue());
                    break;
                default:
                    break;
                }
            }
        }
    }

    /**
     * Populates connection level data from discovery item to support connection data aggregation.
     * @param connectionData Connection data to populate
     * @param connectionDiscoveryItem Corresponding discovery item
     */
    private void populateConnectionData(DiscoveryItemData connectionData, ConnectionDiscoveryItem connectionDiscoveryItem) {
        if (connectionDiscoveryItem != null) {
            if (!connectionData.containsData(CONNECTION_HOST)) {
                connectionData.setData(CONNECTION_HOST, connectionDiscoveryItem.getHost());
                connectionData.setData(CONNECTION_USER, connectionDiscoveryItem.getUser());
            }
        }
    }

    /**
     * Internal model that breaks SonicMQ metric identity into manageable parts.
     */
    private class MetricSpecifier {
        /** Metric name. */
        private String metric;
        /** Targeted entity name (e.g. queue or connection name). */
        private String entity;
        /** Discovery item class for target entity. */
        private DiscoveryItemClass itemClass;

        public MetricSpecifier(IMetricIdentity identity, String brokerName) {
            String[] components = identity.getNameComponents();
            if (components[0].equals("broker")) {
                metric = identity.getName();
                entity = brokerName;
                itemClass = DiscoveryItemClass.Broker;
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < components.length - 1; i++) {
                    if (sb.length() != 0) {
                        sb.append('.');
                    }
                    sb.append(components[i]);
                }
                metric = sb.toString();
                // NOTE: relies on assumption that last part is connectionID, see
                // SonicUtil.getIdentifier(IConnectionData)
                entity = sha1hex(components[components.length - 1]);
                if (metric.startsWith("connection")) {
                    itemClass = DiscoveryItemClass.Connection;
                } else {
                    itemClass = DiscoveryItemClass.Queue;
                }
            }
        }

        public String getMetric() {
            return metric;
        }

        public String getEntity() {
            return entity;
        }

        public DiscoveryItemClass getItemClass() {
            return itemClass;
        }
    }
}

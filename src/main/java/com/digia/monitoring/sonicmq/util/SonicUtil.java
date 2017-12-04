package com.digia.monitoring.sonicmq.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonicsw.mq.common.runtime.IConnectionData;
import com.sonicsw.mq.common.runtime.IConnectionMemberDetails;
import com.sonicsw.mq.common.runtime.IConnectionMemberInfo;
import com.sonicsw.mq.common.runtime.IConnectionTreeNode;
import com.sonicsw.mq.common.runtime.IDestination;
import com.sonicsw.mq.common.runtime.IQueueData;
import com.sonicsw.mq.common.runtime.ISubscriberData;
import com.sonicsw.mq.common.runtime.JMSObjectFactory;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;

import static com.digia.monitoring.sonicmq.util.DigestUtil.*;

/**
 * <p>Provides convenience methods for accessing data provided by SonicMQ proxies.</p>
 * 
 * @author Sami Pajunen
 */
@SuppressWarnings("unchecked") // Hide lack of generics in SonicMQ interfaces
public class SonicUtil {
	
	private static Logger logger = LoggerFactory.getLogger(SonicUtil.class);

	/** Maximum tree depth to retrieve. */
	private static final int MAX_TREE_DEPTH = 20;

	/**
	 * Retrieves all connections of broker.
	 * @param proxy Proxy
	 * @return All connections of broker
	 */
	public static List<IConnectionData> getAllConnections(IBrokerProxy proxy) {
		List<IConnectionData> data = proxy.getConnections(null);
		if (data != null) {
			return data;
		}
		return Collections.emptyList();
	}
	
	/**
	 * Retrieves all queues of broker.
	 * @param proxy Proxy
	 * @return All queues of proxy
	 */
	public static List<IQueueData> getAllQueues(IBrokerProxy proxy) {
		List<IQueueData> data = proxy.getQueues(null);
		if (data != null) {
			return data;
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieves connection member details of given member's chlidren.
	 * @param proxy Proxy
	 * @param info Connection member info
	 * @return List of connection member details, never null
	 */
	public static List<IConnectionMemberDetails> getChildConnectionMemberDetails(IBrokerProxy proxy, IConnectionMemberInfo info) {
		List<IConnectionMemberDetails> data = proxy.getConnectionMemberDetails(info.getChildRefs());
		if (data != null) {
			return data;
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieves connection subscribers.
	 * @param proxy Proxy
	 * @param connection Connection
	 * @return List of subscribers, never null
	 */
	public static List<ISubscriberData> getSubscribers(IBrokerProxy proxy, IConnectionData connection) {
		ArrayList<ISubscriberData> data = proxy.getSubscribers(connection.getConnectionMemberRef());
		if (data != null) {
			return data;
		}
		return Collections.emptyList();
	}
	
	/**
	 * <p>Retrieves connection tree for given connection member reference.</p>
	 * 
	 * <p>The maximum retrieved tree depth is defined by {@link #MAX_TREE_DEPTH}.</p>
	 * 
	 * @param proxy Proxy
	 * @param memberRef Member reference
	 * @return Connection tree
	 */
	public static IConnectionTreeNode getConnectionTree(IBrokerProxy proxy, Long memberRef) {
		ArrayList<Long> list = new ArrayList<Long>();
		list.add(memberRef);
		return (IConnectionTreeNode) proxy.getConnectionTree(list, MAX_TREE_DEPTH).get(0);
	}

	/**
	 * Retrieves connection tree node children handling no-children case more gracefully.
	 * @param node Connection tree node
	 * @return Child list, empty list if node has no children
	 */
	public static List<IConnectionTreeNode> getConnectionTreeNodeChildren(IConnectionTreeNode node) {
		List<IConnectionTreeNode> children = node.getChildren();
		if (children != null) {
			return children;
		}
		return Collections.emptyList();
	}
	
    /**
     * <p>Attempts to resolve JMS destination name from connection member details.</p>
     * 
     * <p>Logs warning if resolution fails when it shouldn't (i.e. when member should have valid destination name).</p>
     * 
     * @param memberDetails Connectio member details
     * @return JMS destination name or null if unable to resolve or not applicable to member
     */
    public static String getDestinationName(IConnectionMemberDetails memberDetails) {
        try {
            IDestination destination = memberDetails.getDestination();
            if (destination != null) {
                Destination jmsDestination = JMSObjectFactory.createJMSDestination(destination);
                if (jmsDestination instanceof Queue) {
                    return ((Queue) jmsDestination).getQueueName();
                } else if (jmsDestination instanceof Topic) {
                    return ((Topic) jmsDestination).getTopicName();
                } else {
                    logger.warn("Unable to resolve destination name from {} object.", 
                            jmsDestination.getClass().getName());
                }
            }
            return null;
        } catch (JMSException ex) {
            logger.warn("Unable to resolve destination name. JMSException message: {}", ex.getMessage());
            return null;
        }
    }
    
    /**
     * Returns identifier for connection
     * @param connection Connection
     * @return Connection identifier
     */
    public static String getIdentifier(IConnectionData connection) {
    	if (connection.getConnectID() != null) {
    		return sha1hex(connection.getConnectID());
    	} else {
    		return null;
    	}
    }
    
	/**
	 * Returns identifier for subscriber.
	 * @param subscriberData Subscriber
	 * @return Identifier for subscriber
	 */
	public static String getIdentifier(ISubscriberData subscriberData) {
		return sha1hex(subscriberData.getTopicName());
	}
}

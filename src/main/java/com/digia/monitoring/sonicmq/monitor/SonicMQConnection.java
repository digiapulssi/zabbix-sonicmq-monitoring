package com.digia.monitoring.sonicmq.monitor;

import com.sonicsw.mq.common.runtime.IConnectionData;

import static com.digia.monitoring.sonicmq.util.SonicUtil.*;

/**
 * Internal model for discovered SonicMQ connection.
 * @author Sami Pajunen
 *
 */
public class SonicMQConnection {
	/** Broker of connection. */
	private SonicMQComponent broker;
	/** Connection name. */
	private String name;
	/** Connection host. */
	private String host;
	/** Connection user. */
	private String user;
	/** Connection identifier. */
	private String id;
	
	/**
	 * Creates new SonicMQConnection.
	 * @param broker Broker of connection
	 * @param connection Connection data
	 */
	public SonicMQConnection(SonicMQComponent broker, IConnectionData connection) {
		this.broker = broker;
		this.name = connection.getConnectID();
		this.host = connection.getHost();
		this.user = connection.getUser();
		this.id = getIdentifier(connection);
	}
	
	public SonicMQComponent getBroker() {
		return broker;
	}
	
	public String getName() {
		return name;
	}
	
	public String getHost() {
		return host;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getId() {
		return id;
	}
}

package com.digia.monitoring.sonicmq.monitor;

import com.sonicsw.mq.common.runtime.IConnectionData;

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
}

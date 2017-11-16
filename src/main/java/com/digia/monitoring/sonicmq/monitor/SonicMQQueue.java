package com.digia.monitoring.sonicmq.monitor;

import com.sonicsw.mq.common.runtime.IQueueData;

/**
 * Internal model for discovered SonicMQ queue.
 * @author Sami Pajunen
 */
public class SonicMQQueue {
	/** Broker of the queue. */
	private SonicMQComponent broker;
	/** Queue name. */
	private String name;
	
	/**
	 * Creates new SonicMQQueue.
	 * @param broker Broker of queue
	 * @param queue Queue data
	 */
	public SonicMQQueue(SonicMQComponent broker, IQueueData queue) {
		this.broker = broker;
		this.name = queue.getQueueName();
	}
	
	public SonicMQComponent getBroker() {
		return broker;
	}
	
	public String getName() {
		return name;
	}
}

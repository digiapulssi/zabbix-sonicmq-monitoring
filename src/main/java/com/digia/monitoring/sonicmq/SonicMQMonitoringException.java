package com.digia.monitoring.sonicmq;

/**
 * General exception thrown by SonicMQ monitoring classes.
 * @author Sami Pajunen
 *
 */
public class SonicMQMonitoringException extends Exception {

	private static final long serialVersionUID = -737520758703407102L;

	public SonicMQMonitoringException(String message, Throwable cause) {
		super(message, cause);
	}

	public SonicMQMonitoringException(String message) {
		super(message);
	}

}

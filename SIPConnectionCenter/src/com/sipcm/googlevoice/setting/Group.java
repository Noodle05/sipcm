/**
 * 
 */
package com.sipcm.googlevoice.setting;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wgao
 * 
 */
public class Group implements Serializable {
	private static final long serialVersionUID = -2719868681865504624L;
	private int id;
	private String name;
	private Map<Integer, Boolean> disabledForwardingIds;
	private boolean isCustomForwarding;
	private boolean isCustomGreeting;
	private boolean isCustomDirectConnect;
	private boolean directConnection;
	private int greetingId;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the disabledForwardingIds
	 */
	public Map<Integer, Boolean> getDisabledForwardingIds() {
		return disabledForwardingIds;
	}

	/**
	 * @param disabledForwardingIds
	 *            the disabledForwardingIds to set
	 */
	public void setDisabledForwardingIds(
			Map<Integer, Boolean> disabledForwardingIds) {
		this.disabledForwardingIds = disabledForwardingIds;
	}

	/**
	 * @return the isCustomForwarding
	 */
	public boolean isCustomForwarding() {
		return isCustomForwarding;
	}

	/**
	 * @param isCustomForwarding
	 *            the isCustomForwarding to set
	 */
	public void setCustomForwarding(boolean isCustomForwarding) {
		this.isCustomForwarding = isCustomForwarding;
	}

	/**
	 * @return the isCustomGreeting
	 */
	public boolean isCustomGreeting() {
		return isCustomGreeting;
	}

	/**
	 * @param isCustomGreeting
	 *            the isCustomGreeting to set
	 */
	public void setCustomGreeting(boolean isCustomGreeting) {
		this.isCustomGreeting = isCustomGreeting;
	}

	/**
	 * @return the isCustomDirectConnect
	 */
	public boolean isCustomDirectConnect() {
		return isCustomDirectConnect;
	}

	/**
	 * @param isCustomDirectConnect
	 *            the isCustomDirectConnect to set
	 */
	public void setCustomDirectConnect(boolean isCustomDirectConnect) {
		this.isCustomDirectConnect = isCustomDirectConnect;
	}

	/**
	 * @return the directConnection
	 */
	public boolean isDirectConnection() {
		return directConnection;
	}

	/**
	 * @param directConnection
	 *            the directConnection to set
	 */
	public void setDirectConnection(boolean directConnection) {
		this.directConnection = directConnection;
	}

	/**
	 * @return the greetingId
	 */
	public int getGreetingId() {
		return greetingId;
	}

	/**
	 * @param greetingId
	 *            the greetingId to set
	 */
	public void setGreetingId(int greetingId) {
		this.greetingId = greetingId;
	}
}

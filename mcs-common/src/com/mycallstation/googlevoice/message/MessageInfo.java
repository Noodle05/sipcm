/**
 * 
 */
package com.mycallstation.googlevoice.message;

import java.io.Serializable;

/**
 * https://www.google.com/voice/xpc/checkMessages?r=XK2%2FBiyhIWf9VV4p%2
 * BtQnI5G%2BVI0%3D
 * 
 * @author Wei Gao
 */
public class MessageInfo implements Serializable {
	private static final long serialVersionUID = -2431761651675419773L;
	private int all;
	private int inbox;
	private int missed;
	private int placed;
	private int received;
	private int recorded;
	private int trash;
	private int unread;
	private int voicemail;

	/**
	 * @return the all
	 */
	public int getAll() {
		return all;
	}

	/**
	 * @param all
	 *            the all to set
	 */
	public void setAll(int all) {
		this.all = all;
	}

	/**
	 * @return the inbox
	 */
	public int getInbox() {
		return inbox;
	}

	/**
	 * @param inbox
	 *            the inbox to set
	 */
	public void setInbox(int inbox) {
		this.inbox = inbox;
	}

	/**
	 * @return the missed
	 */
	public int getMissed() {
		return missed;
	}

	/**
	 * @param missed
	 *            the missed to set
	 */
	public void setMissed(int missed) {
		this.missed = missed;
	}

	/**
	 * @return the placed
	 */
	public int getPlaced() {
		return placed;
	}

	/**
	 * @param placed
	 *            the placed to set
	 */
	public void setPlaced(int placed) {
		this.placed = placed;
	}

	/**
	 * @return the received
	 */
	public int getReceived() {
		return received;
	}

	/**
	 * @param received
	 *            the received to set
	 */
	public void setReceived(int received) {
		this.received = received;
	}

	/**
	 * @return the recorded
	 */
	public int getRecorded() {
		return recorded;
	}

	/**
	 * @param recorded
	 *            the recorded to set
	 */
	public void setRecorded(int recorded) {
		this.recorded = recorded;
	}

	/**
	 * @return the trash
	 */
	public int getTrash() {
		return trash;
	}

	/**
	 * @param trash
	 *            the trash to set
	 */
	public void setTrash(int trash) {
		this.trash = trash;
	}

	/**
	 * @return the unread
	 */
	public int getUnread() {
		return unread;
	}

	/**
	 * @param unread
	 *            the unread to set
	 */
	public void setUnread(int unread) {
		this.unread = unread;
	}

	/**
	 * @return the voicemail
	 */
	public int getVoicemail() {
		return voicemail;
	}

	/**
	 * @param voicemail
	 *            the voicemail to set
	 */
	public void setVoicemail(int voicemail) {
		this.voicemail = voicemail;
	}
}

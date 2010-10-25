/**
 * 
 */
package com.sipcm.email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Jack
 * 
 */
public class EmailBean implements Serializable {
	private static final long serialVersionUID = 6737369416708241669L;

	private Collection<String> toAddress;

	private Collection<String> ccAddress;

	private Collection<String> bccAddress;

	private String fromAddress;

	private String replyAddress;

	private String subject;

	private String body;

	private String charSet;

	private EmailPriority priority;

	private Collection<DataSource> attachments;

	private boolean htmlEncoded;

	/**
	 * Email priority enumeration.
	 * 
	 * @author Wei Gao
	 */
	public static enum EmailPriority {
		VERYHIGH(1), HIGH(2), NORMAL(3), LOW(4), VERYLOW(5);
		private int value;

		private EmailPriority(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static EmailPriority valueOf(int value) {
			switch (value) {
			case 1:
				return VERYHIGH;
			case 2:
				return HIGH;
			case 4:
				return LOW;
			case 5:
				return VERYLOW;
			default:
				return NORMAL;
			}
		}
	}

	/**
	 * Default constructor
	 */
	public EmailBean() {
		toAddress = new ArrayList<String>();
		ccAddress = new ArrayList<String>();
		bccAddress = new ArrayList<String>();
		fromAddress = null;
		replyAddress = null;
		subject = null;
		body = null;
		charSet = null;
		priority = EmailPriority.NORMAL;
		attachments = new ArrayList<DataSource>();
		htmlEncoded = false;
	}

	/**
	 * Get bcc address.
	 * 
	 * @return bcc address
	 */
	public Collection<String> getBccAddress() {
		return bccAddress;
	}

	/**
	 * Clear bcc address
	 */
	void clearBccAddress() {
		bccAddress.clear();
	}

	/**
	 * Add bcc address
	 * 
	 * @param address
	 */
	public void addBccAddress(String address) {
		bccAddress.add(address);
	}

	/**
	 * Get body of the email.
	 * 
	 * @return email body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Get cc address.
	 * 
	 * @return cc address
	 */
	public Collection<String> getCcAddress() {
		return ccAddress;
	}

	/**
	 * Clear cc address
	 */
	void clearCcAddress() {
		ccAddress.clear();
	}

	/**
	 * Add cc address
	 * 
	 * @param address
	 */
	public void addCcAddress(String address) {
		ccAddress.add(address);
	}

	/**
	 * Get from address.
	 * 
	 * @return from address
	 */
	public String getFromAddress() {
		return fromAddress;
	}

	/**
	 * Check if this email should be encode into HTML
	 * 
	 * @return true if HTML encoded email, false otherwise.
	 */
	public boolean isHtmlEncoded() {
		return htmlEncoded;
	}

	/**
	 * Get reply address.
	 * 
	 * @return replay address
	 */
	public String getReplyAddress() {
		return replyAddress;
	}

	/**
	 * Get subject.
	 * 
	 * @return subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Get to address.
	 * 
	 * @return to address
	 */
	public Collection<String> getToAddress() {
		return toAddress;
	}

	/**
	 * Clear to address
	 */
	void clearToAddress() {
		toAddress.clear();
	}

	/**
	 * Add to address
	 * 
	 * @param address
	 */
	public void addToAddress(String address) {
		toAddress.add(address);
	}

	/**
	 * Set subject. Subject is required.
	 * 
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Set reply address.
	 * 
	 * @param replyAddress
	 */
	public void setReplyAddress(String replyAddress) {
		this.replyAddress = replyAddress;
	}

	/**
	 * Set if email body should encode into HTML
	 * 
	 * @param htmlEncoded
	 */
	public void setHtmlEncoded(boolean htmlEncoded) {
		this.htmlEncoded = htmlEncoded;
	}

	/**
	 * Set from address.
	 * 
	 * @param fromAddress
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	/**
	 * Set body of email. Body is required.
	 * 
	 * @param body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Get character set of the email.
	 * 
	 * @return
	 */
	public String getCharSet() {
		return charSet;
	}

	/**
	 * Set character set of the email.
	 * 
	 * @param charSet
	 */
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	/**
	 * Get priority.
	 * 
	 * @return priority
	 */
	public EmailPriority getPriority() {
		return priority;
	}

	/**
	 * Set priority.
	 * 
	 * @param priority
	 */
	public void setPriority(EmailPriority priority) {
		this.priority = priority;
	}

	/**
	 * Set priority.
	 * 
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = EmailPriority.valueOf(priority);
	}

	/**
	 * Get list of attachments.
	 * 
	 * @return attachments
	 */
	Collection<DataSource> getAttachments() {
		return attachments;
	}

	/**
	 * Clear attachments.
	 */
	void clearAttachments() {
		attachments.clear();
	}

	/**
	 * Add file as attachment.
	 * 
	 * @param filename
	 */
	public void addAttachment(String filename) {
		DataSource ds = new FileDataSource(filename);
		attachments.add(ds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("From", fromAddress);
		tsb.append("To", toAddress);
		tsb.append("Cc", ccAddress);
		tsb.append("Bcc", bccAddress);
		tsb.append("Reply", replyAddress);
		tsb.append("Subject", subject);
		tsb.append("Html encode", htmlEncoded);
		tsb.append("Priority", priority.name());
		if (attachments.size() > 0) {
			tsb.append("Attachments", attachments.size());
		}
		return tsb.toString();
	}
}

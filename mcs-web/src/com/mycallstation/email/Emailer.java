/**
 * 
 */
package com.mycallstation.email;

/**
 * @author Jack
 * 
 */
public interface Emailer {
	/**
	 * Use this API to send email notification. User should always use this API
	 * for notification purpose. Will return true if emailBean been added into
	 * email queue successfully. Otherwise it will return false. Add into email
	 * queue doesn't means the email had been sent out successfully. It depend
	 * on if SMTP setting is correct as well. Please check system log if miss
	 * email.
	 * 
	 * @param emailBean
	 * @return if sent email success.
	 */
	public boolean sendMail(EmailBean emailBean);

	/**
	 * Enable email notification service.
	 */
	public void enableEmailService();

	/**
	 * Disable email notification service.
	 */
	public void disableEmailService();

	/**
	 * Get how many email bean been proceed.
	 * 
	 * @return
	 */
	public long getTotalProceed();

	/**
	 * Get how many email bean been proceed successfully
	 * 
	 * @return
	 */
	public long getTotalSucceed();

	/**
	 * Get how many email bean been proceed failed
	 * 
	 * @return
	 */

	public long getTotalFailed();

	/**
	 * Reset processor counter
	 */
	public void resetCounter();

}

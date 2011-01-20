package com.sipcm.email;

public interface EmailProcessor {

	public void process();

	public boolean addEmail(EmailBean _email);

	/**
	 * @return total proceed email bean
	 */
	public long getTotalProceed();

	/**
	 * @return total successfully proceed email bean
	 */
	public long getTotalSucceed();

	/**
	 * @return total failed proceed email bean
	 */
	public long getTotalFailed();

	public void resetCounter();

}
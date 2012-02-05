/**
 * 
 */
package com.mycallstation.email.receiver;

/**
 * @author Wei Gao
 * 
 */
public abstract class MailSessionManager {
	protected abstract MailSession createMailSession();

	public MailSession getGmailSession(String account, String password) {
		MailSession session = createMailSession();
		session.setHost("imap.gmail.com");
		session.setProtocol("imaps");
		session.setAccount(account);
		session.setPassword(password);
		return session;
	}
}

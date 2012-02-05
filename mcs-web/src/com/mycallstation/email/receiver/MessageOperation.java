/**
 * 
 */
package com.mycallstation.email.receiver;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * @author Wei Gao
 * 
 */
public interface MessageOperation {
	public void execute(Message[] messages) throws MessagingException,
			IOException;
}

/**
 * 
 */
package com.mycallstation.web;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.mycallstation.web.util.Messages;

/**
 * @author Wei Gao
 * 
 */
@Component("successPageBean")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class SuccessPageBean implements Serializable {
	private static final long serialVersionUID = 7929416070174508892L;

	@Resource(name = "web.messages")
	private Messages messages;

	private String messageId;

	public void prepareMessage() {
		if (messageId != null) {
			FacesMessage message = messages.getMessage(messageId,
					FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	/**
	 * @param messageId
	 *            the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}
}

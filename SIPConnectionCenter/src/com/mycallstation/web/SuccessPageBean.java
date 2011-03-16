/**
 * 
 */
package com.mycallstation.web;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.mycallstation.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "successPageBean")
@RequestScoped
public class SuccessPageBean implements Serializable {
	private static final long serialVersionUID = 7929416070174508892L;

	private String messageId;

	public void prepareMessage() {
		if (messageId != null) {
			FacesMessage message = Messages.getMessage(messageId,
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

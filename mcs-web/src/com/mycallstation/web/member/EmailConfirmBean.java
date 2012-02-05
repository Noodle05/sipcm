/**
 * 
 */
package com.mycallstation.web.member;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.mycallstation.constant.ActiveMethod;
import com.mycallstation.dataaccess.business.RoleService;
import com.mycallstation.dataaccess.business.UserActivationService;
import com.mycallstation.dataaccess.business.UserService;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserActivation;
import com.mycallstation.web.util.Messages;

/**
 * @author Wei Gao
 * 
 */
@Component("emailConfirmBean")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class EmailConfirmBean implements Serializable {
	private static final long serialVersionUID = -8821859014971422150L;

	private static final Logger logger = LoggerFactory
			.getLogger(EmailConfirmBean.class);

	@Resource(name = "userActivationService")
	private UserActivationService userActivationService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "roleService")
	private RoleService roleService;

	@Resource(name = "web.messages")
	private Messages messages;

	private Long userId;

	private String activeCode;

	public void confirm(ComponentSystemEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Confirm email for user id: {}, active code: \"{}\"",
					userId, activeCode);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		if (userId == null) {
			FacesMessage message = messages.getMessage(
					"member.email.confirm.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		User user = userService.fullyLoadUser(userId);
		if (user == null) {
			FacesMessage message = messages.getMessage(
					"member.email.confirm.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		UserActivation ua = userActivationService.getUserActivationByUser(user);
		if (ua == null) {
			FacesMessage message = messages.getMessage(
					"member.email.confirm.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (ua.getExpireDate().before(new Date())) {
			FacesMessage message = messages.getMessage(
					"member.email.confirm.error.expired",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (ActiveMethod.ADMIN.equals(ua.getMethod())) {
			// Admin active, check if current user is admin
			if (!fc.getExternalContext().isUserInRole(RoleService.ADMIN_ROLE)) {
				FacesMessage message = messages.getMessage(
						"member.email.confirm.error.onlyadmin",
						FacesMessage.SEVERITY_ERROR);
				fc.addMessage(null, message);
				return;
			} else {
				activeCode = ua.getActiveCode();
			}
		}
		if (activeCode == null) {
			FacesMessage message = messages.getMessage(
					"member.email.confirm.error.invalidactivecode",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (!ua.getActiveCode().equals(activeCode)) {
			FacesMessage message = messages.getMessage(
					"member.email.confirm.error.invalidactivecode",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		userActivationService.removeEntity(ua);
		user.addRole(roleService.getCallerRole());
		userService.saveEntity(user);
		FacesMessage message = messages.getMessage(
				"member.email.confirm.success", FacesMessage.SEVERITY_INFO);
		fc.addMessage(null, message);
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param activeCode
	 *            the activeCode to set
	 */
	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	/**
	 * @return the activeCode
	 */
	public String getActiveCode() {
		return activeCode;
	}
}

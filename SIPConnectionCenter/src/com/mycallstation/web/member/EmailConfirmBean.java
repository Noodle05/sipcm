/**
 * 
 */
package com.mycallstation.web.member;

import java.io.Serializable;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.common.ActiveMethod;
import com.mycallstation.common.business.RoleService;
import com.mycallstation.common.business.UserActivationService;
import com.mycallstation.common.business.UserService;
import com.mycallstation.common.model.User;
import com.mycallstation.common.model.UserActivation;
import com.mycallstation.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "emailConfirmBean")
@RequestScoped
public class EmailConfirmBean implements Serializable {
	private static final long serialVersionUID = -8821859014971422150L;

	private static final Logger logger = LoggerFactory
			.getLogger(EmailConfirmBean.class);

	@ManagedProperty(value = "#{userActivationService}")
	private transient UserActivationService userActivationService;

	@ManagedProperty(value = "#{userService}")
	private transient UserService userService;

	@ManagedProperty(value = "#{roleService}")
	private transient RoleService roleService;

	private Long userId;

	private String activeCode;

	public void confirm(ComponentSystemEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Confirm email for user id: {}, active code: \"{}\"",
					userId, activeCode);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		if (userId == null) {
			FacesMessage message = Messages.getMessage(
					"member.email.confirm.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		User user = userService.fullyLoadUser(userId);
		if (user == null) {
			FacesMessage message = Messages.getMessage(
					"member.email.confirm.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		UserActivation ua = userActivationService.getUserActivationByUser(user);
		if (ua == null) {
			FacesMessage message = Messages.getMessage(
					"member.email.confirm.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (ua.getExpireDate().before(new Date())) {
			FacesMessage message = Messages.getMessage(
					"member.email.confirm.error.expired",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (ActiveMethod.ADMIN.equals(ua.getMethod())) {
			// Admin active, check if current user is admin
			if (!fc.getExternalContext().isUserInRole(RoleService.ADMIN_ROLE)) {
				FacesMessage message = Messages.getMessage(
						"member.email.confirm.error.onlyadmin",
						FacesMessage.SEVERITY_ERROR);
				fc.addMessage(null, message);
				return;
			} else {
				activeCode = ua.getActiveCode();
			}
		}
		if (activeCode == null) {
			FacesMessage message = Messages.getMessage(
					"member.email.confirm.error.invalidactivecode",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (!ua.getActiveCode().equals(activeCode)) {
			FacesMessage message = Messages.getMessage(
					"member.email.confirm.error.invalidactivecode",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		userActivationService.removeEntity(ua);
		user.addRole(roleService.getCallerRole());
		userService.saveEntity(user);
		FacesMessage message = Messages.getMessage(
				"member.email.confirm.success", FacesMessage.SEVERITY_INFO);
		fc.addMessage(null, message);
	}

	/**
	 * @param userActivationService
	 *            the userActivationService to set
	 */
	public void setUserActivationService(
			UserActivationService userActivationService) {
		this.userActivationService = userActivationService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @param roleService
	 *            the roleService to set
	 */
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
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

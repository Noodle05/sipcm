/**
 * 
 */
package com.mycallstation.web.account;

import java.io.Serializable;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.constant.AccountStatus;
import com.mycallstation.constant.ActiveMethod;
import com.mycallstation.dataaccess.business.RoleService;
import com.mycallstation.dataaccess.business.UserActivationService;
import com.mycallstation.dataaccess.business.UserService;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserActivation;
import com.mycallstation.web.util.JSFUtils;
import com.mycallstation.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "activationBean")
@RequestScoped
public class ActivationBean implements Serializable {
	private static final long serialVersionUID = 6392210650635418763L;

	private static final Logger logger = LoggerFactory
			.getLogger(ActivationBean.class);

	private Long userId;

	private String activeCode;

	public void activeUser(ComponentSystemEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Active user with user id: {}, active code: \"{}\"",
					userId, activeCode);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		UserService userService = JSFUtils.getUserService();
		UserActivationService userActivationService = JSFUtils
				.getUserActivationService();
		RoleService roleService = JSFUtils.getRoleService();
		if (userId == null) {
			FacesMessage message = Messages.getMessage(
					"account.active.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		User user = userService.fullyLoadUser(userId);
		if (user == null || user.getDeleteDate() != null) {
			FacesMessage message = Messages.getMessage(
					"account.active.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("User object: \"{}\"", user);
		}
		UserActivation ua = userActivationService.getUserActivationByUser(user);
		if (ua == null) {
			FacesMessage message = Messages.getMessage(
					"account.active.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("User Activation object: \"{}\"", ua);
		}
		if (!AccountStatus.PENDING.equals(user.getStatus())) {
			userActivationService.removeEntity(ua);
			FacesMessage message = Messages.getMessage(
					"account.active.error.notpending",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (!fc.getExternalContext().isUserInRole(RoleService.ADMIN_ROLE)
				&& ua.getExpireDate().before(new Date())) {
			if (logger.isTraceEnabled()) {
				logger.trace("User active object alread expired");
			}
			FacesMessage message = Messages
					.getMessage("account.active.error.expired",
							FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (ActiveMethod.ADMIN.equals(ua.getMethod())) {
			// Admin active, check if current user is admin
			if (!fc.getExternalContext().isUserInRole(RoleService.ADMIN_ROLE)) {
				FacesMessage message = Messages.getMessage(
						"account.active.error.onlyadmin",
						FacesMessage.SEVERITY_ERROR);
				fc.addMessage(null, message);
				return;
			} else {
				activeCode = ua.getActiveCode();
			}
		}
		if (activeCode == null) {
			FacesMessage message = Messages.getMessage(
					"account.active.error.invalidactivecode",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (!ua.getActiveCode().equals(activeCode)) {
			FacesMessage message = Messages.getMessage(
					"account.active.error.invalidactivecode",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Remove user activation object.");
		}
		userActivationService.removeEntity(ua);
		if (logger.isTraceEnabled()) {
			logger.trace("User status change to active.");
		}
		user.setStatus(AccountStatus.ACTIVE);
		if (logger.isTraceEnabled()) {
			logger.trace("Add caller role.");
		}
		user.addRole(roleService.getCallerRole());
		userService.saveEntity(user);
		FacesMessage message = Messages.getMessage("activation.success",
				FacesMessage.SEVERITY_INFO);
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

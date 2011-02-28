/**
 * 
 */
package com.sipcm.web.register;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.common.AccountStatus;
import com.sipcm.common.ActiveMethod;
import com.sipcm.common.business.RoleService;
import com.sipcm.common.business.UserActivationService;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.common.model.UserActivation;
import com.sipcm.web.util.Messages;

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

	@ManagedProperty(value = "#{userActivationService}")
	private UserActivationService userActivationService;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	private Long userId;

	private String activeCode;

	private boolean actived;

	@PostConstruct
	public void init() {
		actived = false;
		if (logger.isDebugEnabled()) {
			logger.debug("A new activation bean been created.");
		}
	}

	public void activeUser(ComponentSystemEvent event) {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (userId == null) {
			FacesMessage message = Messages
					.getMessage("account.active.error.invaliduserid");
			fc.addMessage(null, message);
			return;
		}
		User user = userService.getEntityById(userId);
		if (user == null) {
			FacesMessage message = Messages
					.getMessage("account.active.error.invaliduserid");
			fc.addMessage(null, message);
			return;
		}
		UserActivation ua = userActivationService.getUserActivationByUser(user);
		if (ua == null) {
			FacesMessage message = Messages.getMessage(
					"account.active.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (!AccountStatus.PENDING.equals(user.getStatus())) {
			userActivationService.removeEntity(ua);
			FacesMessage message = Messages.getMessage(
					"account.active.error.notpending",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return;
		}
		if (ua.getExpireDate().before(new Date())) {
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
		userActivationService.removeEntity(ua);
		user.setStatus(AccountStatus.ACTIVE);
		userService.saveEntity(user);
		actived = true;
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

	/**
	 * @param actived
	 *            the actived to set
	 */
	public void setActived(boolean actived) {
		this.actived = actived;
	}

	/**
	 * @return the actived
	 */
	public boolean isActived() {
		return actived;
	}
}

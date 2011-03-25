/**
 * 
 */
package com.mycallstation.web.account;

import java.io.Serializable;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@ManagedBean(name = "passwordResetBean")
@RequestScoped
public class PasswordResetBean implements Serializable {
	private static final long serialVersionUID = 5427030371221677230L;
	private static final Logger logger = LoggerFactory
			.getLogger(PasswordResetBean.class);

	@ManagedProperty(value = "#{userService}")
	private transient UserService userService;

	@ManagedProperty(value = "#{userActivationService}")
	private transient UserActivationService userActivationService;

	private String activeCode;
	private Long userId;
	private String password;

	public String save() {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Resetting password for user id: {}, with active code: \"{}\"",
					userId, activeCode);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		if (userId == null) {
			FacesMessage message = Messages.getMessage(
					"password.reset.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return null;
		}
		User user = userService.fullyLoadUser(userId);
		if (user == null || user.getDeleteDate() != null) {
			FacesMessage message = Messages.getMessage(
					"password.reset.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return null;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("User object: \"{}\"", user);
		}
		UserActivation ua = getUserActivationService().getUserActivationByUser(
				user);
		if (ua == null) {
			FacesMessage message = Messages.getMessage(
					"password.reset.error.invaliduserid",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return null;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("User Activation object: \"{}\"", ua);
		}
		if (!fc.getExternalContext().isUserInRole(RoleService.ADMIN_ROLE)
				&& ua.getExpireDate().before(new Date())) {
			if (logger.isTraceEnabled()) {
				logger.trace("User active object alread expired");
			}
			FacesMessage message = Messages
					.getMessage("password.reset.error.expired",
							FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return null;
		}
		if (ActiveMethod.ADMIN.equals(ua.getMethod())) {
			// Admin active, check if current user is admin
			if (!fc.getExternalContext().isUserInRole(RoleService.ADMIN_ROLE)) {
				FacesMessage message = Messages.getMessage(
						"password.reset.error.onlyadmin",
						FacesMessage.SEVERITY_ERROR);
				fc.addMessage(null, message);
				return null;
			} else {
				activeCode = ua.getActiveCode();
			}
		}
		if (activeCode == null) {
			FacesMessage message = Messages.getMessage(
					"password.reset.error.invalidactivecode",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return null;
		}
		if (!ua.getActiveCode().equals(activeCode)) {
			FacesMessage message = Messages.getMessage(
					"password.reset.error.invalidactivecode",
					FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null, message);
			return null;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Remove user activation object.");
		}
		getUserActivationService().removeEntity(ua);
		if (logger.isTraceEnabled()) {
			logger.trace("Reseting user password");
		}
		getUserService().setPassword(user, password);
		userService.saveEntity(user);
		FacesMessage message = Messages.getMessage("password.reset.success",
				FacesMessage.SEVERITY_INFO);
		fc.addMessage(null, message);
		return "success";
	}

	public void validatePassword(ComponentSystemEvent event) {
		FacesContext fc = FacesContext.getCurrentInstance();
		UIComponent components = event.getComponent();
		UIInput passwordTxt = (UIInput) components.findComponent("password");
		UIInput confirmPasswdTxt = (UIInput) components
				.findComponent("confirmPassword");
		if (passwordTxt.isValid() && confirmPasswdTxt.isValid()) {
			String password = passwordTxt.getLocalValue().toString().trim();
			String confirmPasswd = confirmPasswdTxt.getLocalValue().toString()
					.trim();
			if (password != null && confirmPasswd != null) {
				if (!password.equals(confirmPasswd)) {
					FacesMessage message = Messages.getMessage(
							"register.error.password.notmatch",
							FacesMessage.SEVERITY_ERROR);
					fc.addMessage("registrationForm:confirmPassword", message);
					fc.renderResponse();
				}
			}
		}
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
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService() {
		if (userService == null) {
			userService = JSFUtils.getManagedBean("userService",
					UserService.class);
		}
		return userService;
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
	 * @return the userActivationService
	 */
	public UserActivationService getUserActivationService() {
		if (userActivationService == null) {
			userActivationService = JSFUtils.getManagedBean(
					"userActivationService", UserActivationService.class);
		}
		return userActivationService;
	}
}

/**
 * 
 */
package com.sipcm.web.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.sipcm.common.ActiveMethod;
import com.sipcm.common.SystemConfiguration;
import com.sipcm.common.business.UserActivationService;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.common.model.UserActivation;
import com.sipcm.web.util.EmailUtils;
import com.sipcm.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "forgetPasswordFormBean")
@RequestScoped
public class ForgetPasswordFormBean implements Serializable {
	private static final long serialVersionUID = 4492899144455600476L;

	public static final String FORGET_PASSWORD_EMAIL_TEMPLATE = "/templates/forget-password.vm";

	@ManagedProperty("#{userService}")
	private transient UserService userService;

	@ManagedProperty("#{userActivationService}")
	private transient UserActivationService userActivationService;

	@ManagedProperty("#{systemConfiguration}")
	private transient SystemConfiguration appConfig;

	@ManagedProperty(value = "#{webEmailUtils}")
	private transient EmailUtils emailUtils;

	private String email;

	public void action() {
		User user = userService.getUserByEmail(email);
		if (user == null) {
			FacesMessage message = Messages.getMessage("",
					FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		UserActivation ua = userActivationService.getUserActivationByUser(user);
		if (ua == null) {
			ua = userActivationService.createUserActivation(user,
					ActiveMethod.SELF, appConfig.getActiveExpires());
		} else {
			if (ActiveMethod.ADMIN.equals(ua.getMethod())) {
				FacesMessage message = Messages.getMessage("",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return;
			}
			userActivationService.updateExpires(ua,
					appConfig.getActiveExpires());
		}
		userActivationService.saveEntity(ua);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("", ua);
		emailUtils
				.sendMail(user.getEmail(), Messages.getString(null,
						"forget.password.email.subject", null),
						FORGET_PASSWORD_EMAIL_TEMPLATE, params, user
								.getLocale());
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
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
	 * @param appConfig
	 *            the appConfig to set
	 */
	public void setAppConfig(SystemConfiguration appConfig) {
		this.appConfig = appConfig;
	}

	/**
	 * @param emailUtils
	 *            the emailUtils to set
	 */
	public void setEmailUtils(EmailUtils emailUtils) {
		this.emailUtils = emailUtils;
	}
}

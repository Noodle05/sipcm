/**
 * 
 */
package com.mycallstation.web.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.mycallstation.constant.ActiveMethod;
import com.mycallstation.dataaccess.business.UserActivationService;
import com.mycallstation.dataaccess.business.UserService;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserActivation;
import com.mycallstation.web.util.EmailUtils;
import com.mycallstation.web.util.Messages;
import com.mycallstation.web.util.WebConfiguration;

/**
 * @author Wei Gao
 * 
 */
@Component("forgetPasswordFormBean")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ForgetPasswordFormBean implements Serializable {
	private static final long serialVersionUID = 4492899144455600476L;

	public static final String FORGET_PASSWORD_EMAIL_TEMPLATE = "/templates/forget-password.vm";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "userActivationService")
	private UserActivationService userActivationService;

	@Resource(name = "systemConfiguration")
	private WebConfiguration appConfig;

	@Resource(name = "webEmailUtils")
	private EmailUtils emailUtils;

	@Resource(name = "web.messages")
	private Messages messages;

	private String email;

	public String action() {
		User user = userService.getUserByEmail(email);
		if (user == null) {
			FacesMessage message = messages.getMessage(
					"forget.password.error.user.not.found",
					FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(
					"forgetPasswordForm:email", message);
			return null;
		}
		UserActivation ua = userActivationService.getUserActivationByUser(user);
		if (ua == null) {
			ua = userActivationService.createUserActivation(user,
					ActiveMethod.SELF, appConfig.getActiveExpires());
		} else {
			if (ActiveMethod.ADMIN.equals(ua.getMethod())) {
				FacesMessage message = messages.getMessage(
						"forget.password.error.waiting.admin",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(
						"forgetPasswordForm:email", message);
				return null;
			}
			userActivationService.updateExpires(ua,
					appConfig.getActiveExpires());
		}
		userActivationService.saveEntity(ua);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activation", ua);
		params.put("activeExpires", appConfig.getActiveExpires());
		emailUtils
				.sendMail(user.getEmail(), user.getUserDisplayName(),
						messages.getString(null,
								"forget.password.email.subject", null),
						FORGET_PASSWORD_EMAIL_TEMPLATE, params, user
								.getLocale());
		FacesMessage message = messages.getMessage("forget.password.success",
				FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
		return "success";
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
}

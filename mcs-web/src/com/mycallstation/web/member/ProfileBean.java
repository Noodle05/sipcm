/**
 * 
 */
package com.mycallstation.web.member;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mycallstation.constant.ActiveMethod;
import com.mycallstation.dataaccess.business.RoleService;
import com.mycallstation.dataaccess.business.UserActivationService;
import com.mycallstation.dataaccess.business.UserService;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserActivation;
import com.mycallstation.scope.ViewScope;
import com.mycallstation.web.LocaleTimeZoneHolderBean;
import com.mycallstation.web.util.EmailUtils;
import com.mycallstation.web.util.JSFUtils;
import com.mycallstation.web.util.Messages;
import com.mycallstation.web.util.WebConfiguration;

/**
 * @author Wei Gao
 * 
 */
@Component("profileBean")
@Scope(ViewScope.VIEW_SCOPE)
public class ProfileBean implements Serializable {
	private static final long serialVersionUID = 3724475125208917222L;

	private static final Logger logger = LoggerFactory
			.getLogger(ProfileBean.class);

	public static final String CONFIRM_EMAIL_TEMPLATE = "/templates/email-confirm.vm";

	@Resource(name = "systemConfiguration")
	private WebConfiguration appConfig;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "roleService")
	private RoleService roleService;

	@Resource(name = "localeTimeZoneHolderBean")
	private LocaleTimeZoneHolderBean localeTimeZoneHolderBean;

	@Resource(name = "userActivationService")
	private UserActivationService userActivationService;

	@Resource(name = "webEmailUtils")
	private EmailUtils emailUtils;

	@Resource(name = "jsfUtils")
	private JSFUtils jsfUtils;

	@Resource(name = "web.messages")
	private Messages messages;

	private String email;

	private String password;

	private String firstName;

	private String middleName;

	private String lastName;

	private String displayName;

	private String locale;

	private String timeZone;

	private Pattern emailPattern;

	@PostConstruct
	public void init() {
		emailPattern = Pattern.compile(appConfig.getEmailPattern());
		User user = jsfUtils.getCurrentUser();
		if (user == null) {
			throw new IllegalStateException(
					"Calling profile bean without user?");
		}
		email = user.getEmail();
		firstName = user.getFirstName();
		middleName = user.getMiddleName();
		lastName = user.getLastName();
		displayName = user.getDisplayName();
		if (user.getLocale() != null) {
			for (Entry<String, Locale> entry : JSFUtils.availableLocales
					.entrySet()) {
				if (user.getLocale().equals(entry.getValue())) {
					locale = entry.getKey();
					break;
				}
			}
		}
		if (locale == null) {
			locale = JSFUtils.NA;
		}
		if (user.getTimeZone() != null) {
			timeZone = user.getTimeZone().getID();
		} else {
			timeZone = JSFUtils.NA;
		}
	}

	public void save() {
		User user = jsfUtils.getCurrentUser();
		if (logger.isDebugEnabled()) {
			logger.debug("Saving user profile for \"{}\"", user);
		}
		if (password != null && !password.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Password changed.");
			}
			userService.setPassword(user, password);
		}
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);
		user.setLocale(JSFUtils.availableLocales.get(locale));
		if (locale != null) {
			Locale l = JSFUtils.availableLocales.get(locale);
			user.setLocale(l);
		} else {
			user.setLocale(null);
		}
		if (timeZone != null && !JSFUtils.NA.equals(timeZone)) {
			TimeZone tz = TimeZone.getTimeZone(timeZone);
			user.setTimeZone(tz);
		} else {
			user.setTimeZone(null);
		}
		boolean suspend = false;
		if (!email.equalsIgnoreCase(user.getEmail())) {
			if (logger.isTraceEnabled()) {
				logger.trace("Email changed, remove caller role.");
			}
			user.setEmail(email);
			user.removeRole(roleService.getCallerRole());
			suspend = true;
		}
		userService.saveEntity(user);
		if (localeTimeZoneHolderBean != null) {
			localeTimeZoneHolderBean.setLocale(user.getLocale());
			localeTimeZoneHolderBean.setTimeZone(user.getTimeZone());
		}
		if (suspend) {
			changeEmail(user);
			FacesMessage message = messages.getMessage(
					"member.profile.voip.suspend", FacesMessage.SEVERITY_WARN);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		FacesMessage message = messages.getMessage("member.profile.saved",
				FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
		if (suspend) {
			// Since user changed their email, user's role changed, need to
			// logout user.
			if (logger.isTraceEnabled()) {
				logger.trace("Logout user since email changed.");
			}
			FacesContext.getCurrentInstance().getExternalContext()
					.invalidateSession();
			SecurityContextHolder.clearContext();
		}
	}

	public void validateEmail(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String email = ((String) value).trim();
		if (!emailPattern.matcher(email).matches()) {
			FacesMessage message = messages
					.getMessage("register.error.email.pattern",
							FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		User user = userService.getUserByEmail(email);
		if (user != null && !user.equals(jsfUtils.getCurrentUser())) {
			FacesMessage message = messages.getMessage(
					"register.error.email.exists", FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}

	public void validatePassword(ComponentSystemEvent event) {
		FacesContext fc = FacesContext.getCurrentInstance();
		UIComponent components = event.getComponent();
		UIInput passwordTxt = (UIInput) components.findComponent("password");
		UIInput confirmPasswdTxt = (UIInput) components
				.findComponent("confirmPassword");
		if (passwordTxt.isValid() && confirmPasswdTxt.isValid()) {
			if (passwordTxt.getLocalValue() != null
					|| confirmPasswdTxt.getLocalValue() != null) {
				String password = passwordTxt.getLocalValue() == null ? null
						: passwordTxt.getLocalValue().toString().trim();
				String confirmPasswd = confirmPasswdTxt.getLocalValue() == null ? null
						: confirmPasswdTxt.getLocalValue().toString().trim();
				if (password == null) {
					if (confirmPasswd == null) {
						return;
					}
				} else if (password.equals(confirmPasswd)) {
					return;
				}
				FacesMessage message = messages.getMessage(
						"register.error.password.notmatch",
						FacesMessage.SEVERITY_ERROR);
				fc.addMessage("registrationForm:confirmPassword", message);
				fc.renderResponse();
			}
		}
	}

	private void changeEmail(User user) {
		if (logger.isTraceEnabled()) {
			logger.trace("Create user activation object.");
		}
		UserActivation userActivation = userActivationService
				.createUserActivation(user, ActiveMethod.SELF,
						appConfig.getActiveExpires());
		userActivationService.saveEntity(userActivation);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activation", userActivation);
		params.put("activeExpires", appConfig.getActiveExpires());

		if (logger.isTraceEnabled()) {
			logger.trace("Sending confirm email");
		}
		emailUtils.sendMail(user.getEmail(), user.getUserDisplayName(),
				messages.getString(null, "member.email.confirm.subject", null),
				CONFIRM_EMAIL_TEMPLATE, params, user.getLocale());
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
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param middleName
	 *            the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param timeZone
	 *            the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}
}

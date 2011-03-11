/**
 * 
 */
package com.sipcm.web.member;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sipcm.common.ActiveMethod;
import com.sipcm.common.SystemConfiguration;
import com.sipcm.common.business.RoleService;
import com.sipcm.common.business.UserActivationService;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.common.model.UserActivation;
import com.sipcm.web.LocaleTimeZoneHolderBean;
import com.sipcm.web.util.EmailUtils;
import com.sipcm.web.util.JSFUtils;
import com.sipcm.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "profileBean")
@ViewScoped
public class ProfileBean implements Serializable {
	private static final long serialVersionUID = 3724475125208917222L;

	private static final Logger logger = LoggerFactory
			.getLogger(ProfileBean.class);

	public static final String CONFIRM_EMAIL_TEMPLATE = "/templates/email-confirm.vm";

	@ManagedProperty(value = "#{systemConfiguration}")
	private transient SystemConfiguration appConfig;

	@ManagedProperty(value = "#{webEmailUtils}")
	private transient EmailUtils emailUtils;

	@ManagedProperty(value = "#{userService}")
	private transient UserService userService;

	@ManagedProperty(value = "#{roleService}")
	private transient RoleService roleService;

	@ManagedProperty(value = "#{userActivationService}")
	private transient UserActivationService userActivationService;

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
		emailPattern = Pattern.compile(getAppConfig().getEmailPattern());
		User user = JSFUtils.getCurrentUser();
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
		User user = JSFUtils.getCurrentUser();
		if (logger.isDebugEnabled()) {
			logger.debug("Saving user profile for \"{}\"", user);
		}
		if (password != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Password changed.");
			}
			getUserService().setPassword(user, password);
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
			user.removeRole(getRoleService().getCallerRole());
			suspend = true;
		}
		getUserService().saveEntity(user);
		LocaleTimeZoneHolderBean b = JSFUtils.getManagedBean(
				"localeTimeZoneHolderBean", LocaleTimeZoneHolderBean.class);
		if (b != null) {
			b.setLocale(user.getLocale());
			b.setTimeZone(user.getTimeZone());
		}
		if (suspend) {
			changeEmail(user);
			FacesMessage message = Messages.getMessage(
					"member.profile.voip.suspend", FacesMessage.SEVERITY_WARN);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		FacesMessage message = Messages.getMessage("member.profile.saved",
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
			FacesMessage message = Messages
					.getMessage("register.error.email.pattern",
							FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		User user = getUserService().getUserByEmail(email);
		if (user != null && !user.equals(JSFUtils.getCurrentUser())) {
			FacesMessage message = Messages.getMessage(
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
				FacesMessage message = Messages.getMessage(
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
		UserActivation userActivation = getUserActivationService()
				.createUserActivation(user, ActiveMethod.SELF,
						getAppConfig().getActiveExpires());
		getUserActivationService().saveEntity(userActivation);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activation", userActivation);
		params.put("activeExpires", appConfig.getActiveExpires());

		if (logger.isTraceEnabled()) {
			logger.trace("Sending confirm email");
		}
		getEmailUtils().sendMail(user.getEmail(), user.getUserDisplayName(),
				Messages.getString(null, "member.email.confirm.subject", null),
				CONFIRM_EMAIL_TEMPLATE, params, user.getLocale());
	}

	/**
	 * @param appConfig
	 *            the appConfig to set
	 */
	public void setAppConfig(SystemConfiguration appConfig) {
		this.appConfig = appConfig;
	}

	private SystemConfiguration getAppConfig() {
		if (appConfig == null) {
			appConfig = JSFUtils.getManagedBean("systemConfiguration",
					SystemConfiguration.class);
		}
		return appConfig;
	}

	/**
	 * @param emailUtils
	 *            the emailUtils to set
	 */
	public void setEmailUtils(EmailUtils emailUtils) {
		this.emailUtils = emailUtils;
	}

	private EmailUtils getEmailUtils() {
		if (emailUtils == null) {
			emailUtils = JSFUtils.getManagedBean("webEmailUtils",
					EmailUtils.class);
		}
		return emailUtils;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private UserService getUserService() {
		if (userService == null) {
			userService = JSFUtils.getManagedBean("userService",
					UserService.class);
		}
		return userService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	private RoleService getRoleService() {
		if (roleService == null) {
			roleService = JSFUtils.getManagedBean("roleService",
					RoleService.class);
		}
		return roleService;
	}

	/**
	 * @param userActivationService
	 *            the userActivationService to set
	 */
	public void setUserActivationService(
			UserActivationService userActivationService) {
		this.userActivationService = userActivationService;
	}

	private UserActivationService getUserActivationService() {
		if (userActivationService == null) {
			userActivationService = JSFUtils.getManagedBean(
					"userActivationService", UserActivationService.class);
		}
		return userActivationService;
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

	public List<String> getAvailableLocales() {
		return JSFUtils.getAvailableLocales();
	}

	public SelectItem[] getAvailableTimeZones() {
		return JSFUtils.getAvailableTimeZones();
	}
}

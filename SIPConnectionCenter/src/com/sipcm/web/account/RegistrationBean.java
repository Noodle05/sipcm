/**
 * 
 */
package com.sipcm.web.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.common.AccountStatus;
import com.sipcm.common.ActiveMethod;
import com.sipcm.common.SystemConfiguration;
import com.sipcm.common.business.RoleService;
import com.sipcm.common.business.UserActivationService;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.Role;
import com.sipcm.common.model.User;
import com.sipcm.common.model.UserActivation;
import com.sipcm.web.util.EmailUtils;
import com.sipcm.web.util.JSFUtils;
import com.sipcm.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "registrationBean")
@SessionScoped
public class RegistrationBean implements Serializable {
	private static final long serialVersionUID = -6419289187735553748L;

	private static final Logger logger = LoggerFactory
			.getLogger(RegistrationBean.class);

	public static final String SELF_ACTIVE_EMAIL_TEMPLATE = "/templates/self-active.vm";
	public static final String ADMIN_ACTIVE_EMAIL_TEMPLATE = "/templates/admin-active.vm";

	public static final String NA = "---";

	private static final String[] availableTimeZones = new String[] { NA,
			"Pacific/Midway", "US/Hawaii", "US/Alaska", "US/Pacific",
			"America/Tijuana", "US/Arizona", "America/Chihuahua",
			"US/Mountain", "America/Guatemala", "US/Central",
			"America/Mexico_City", "Canada/Saskatchewan", "America/Bogota",
			"US/Eastern", "US/East-Indiana", "Canada/Eastern",
			"America/Caracas", "America/Manaus", "America/Santiago",
			"Canada/Newfoundland", "Brazil/East", "America/Buenos_Aires",
			"America/Godthab", "America/Montevideo", "Atlantic/South_Georgia",
			"Atlantic/Azores", "Atlantic/Cape_Verde", "Africa/Casablanca",
			"Europe/London", "Europe/Berlin", "Europe/Belgrade",
			"Europe/Brussels", "Europe/Warsaw", "Africa/Algiers", "Asia/Amman",
			"Europe/Athens", "Asia/Beirut", "Africa/Cairo", "Africa/Harare",
			"Europe/Helsinki", "Asia/Jerusalem", "Europe/Minsk",
			"Africa/Windhoek", "Asia/Baghdad", "Asia/Kuwait", "Europe/Moscow",
			"Africa/Nairobi", "Asia/Tbilisi", "Asia/Tehran", "Asia/Muscat",
			"Asia/Baku", "Asia/Yerevan", "Asia/Kabul", "Asia/Yekaterinburg",
			"Asia/Karachi", "Asia/Calcutta", "Asia/Colombo", "Asia/Katmandu",
			"Asia/Novosibirsk", "Asia/Dhaka", "Asia/Rangoon", "Asia/Bangkok",
			"Asia/Krasnoyarsk", "Asia/Hong_Kong", "Asia/Irkutsk",
			"Asia/Kuala_Lumpur", "Australia/Perth", "Asia/Taipei",
			"Asia/Tokyo", "Asia/Seoul", "Asia/Yakutsk", "Australia/Adelaide",
			"Australia/Darwin", "Australia/Brisbane", "Australia/Sydney",
			"Pacific/Guam", "Australia/Hobart", "Asia/Vladivostok",
			"Asia/Magadan", "Pacific/Auckland", "Pacific/Fiji",
			"Pacific/Tongatapu" };
	static {
		Arrays.sort(availableTimeZones);
	}

	private static final Map<String, Locale> availableLocales = new LinkedHashMap<String, Locale>();
	static {
		availableLocales.put(NA, null);
		availableLocales.put(Locale.US.getDisplayCountry(Locale.US), Locale.US);
		availableLocales.put(Locale.CHINA.getDisplayCountry(Locale.CHINA),
				Locale.CHINA);
	}

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

	private String username;

	private String email;

	private String password;

	private String firstName;

	private String middleName;

	private String lastName;

	private String displayName;

	private String locale;

	private String timeZone;

	private Pattern usernamePattern;

	private Pattern emailPattern;

	@PostConstruct
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("A new registration bean been created.");
		}
		usernamePattern = Pattern.compile(getAppConfig().getUsernamePattern());
		emailPattern = Pattern.compile(getAppConfig().getEmailPattern());
	}

	public String register() {
		FacesContext context = FacesContext.getCurrentInstance();
		User user = getUserService().createNewEntity();
		user.setUsername(username);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);
		if (locale != null) {
			Locale l = availableLocales.get(locale);
			user.setLocale(l);
		}
		if (timeZone != null && !NA.equals(timeZone)) {
			TimeZone tz = TimeZone.getTimeZone(timeZone);
			user.setTimeZone(tz);
		}
		if (ActiveMethod.NONE.equals(getAppConfig().getActiveMethod())) {
			user.setStatus(AccountStatus.ACTIVE);
		}
		Role userRole = getRoleService().getUserRole();
		user.addRole(userRole);
		getUserService().setPassword(user, password);
		getUserService().saveEntity(user);
		FacesMessage message;
		switch (getAppConfig().getActiveMethod()) {
		case SELF:
			selfActive(user);
			message = Messages.getMessage("register.success.self.active",
					FacesMessage.SEVERITY_INFO);
			context.addMessage(null, message);
			break;
		case ADMIN:
			adminActive(user);
			message = Messages.getMessage("register.success.admin.active",
					FacesMessage.SEVERITY_INFO);
			context.addMessage(null, message);
			break;
		default:
			message = Messages.getMessage("register.success",
					FacesMessage.SEVERITY_INFO);
			context.addMessage(null, message);
			break;
		}
		context.getExternalContext().getSessionMap().remove("registrationBean");
		return "success";
	}

	public void validateUsername(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String username = ((String) value).trim();
		if (username.length() < 6 || username.length() > 32) {
			FacesMessage message = Messages.getMessage(
					"register.error.username.length",
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		if (!usernamePattern.matcher(username).matches()) {
			FacesMessage message = Messages.getMessage(
					"register.error.username.pattern",
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		User user = getUserService().getUserByUsername(username);
		if (user != null) {
			FacesMessage message = Messages.getMessage(
					"register.error.username.exists",
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
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
		if (user != null) {
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

	/**
	 * @param roleService
	 *            the roleService to set
	 */
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
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
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
		return new ArrayList<String>(availableLocales.keySet());
	}

	public String[] getAvailableTimeZones() {
		return availableTimeZones;
	}

	private void selfActive(User user) {
		UserActivation userActivation = getUserActivationService()
				.createUserActivation(user, ActiveMethod.SELF,
						getAppConfig().getActiveExpires());
		getUserActivationService().saveEntity(userActivation);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activation", userActivation);

		getEmailUtils().sendMail(
				user.getEmail(),
				Messages.getString(null, "register.active.self.email.subject",
						null), SELF_ACTIVE_EMAIL_TEMPLATE, params,
				user.getLocale());
	}

	private void adminActive(User user) {
		UserActivation userActivation = getUserActivationService()
				.createUserActivation(user, ActiveMethod.ADMIN,
						getAppConfig().getActiveExpires());
		getUserActivationService().saveEntity(userActivation);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activation", userActivation);

		getEmailUtils().sendMail(
				getAppConfig().getAdminEmail(),
				Messages.getString(null, "register.active.admin.email.subject",
						null), ADMIN_ACTIVE_EMAIL_TEMPLATE, params,
				user.getLocale());
	}
}
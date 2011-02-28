/**
 * 
 */
package com.sipcm.web.register;

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

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.common.AccountStatus;
import com.sipcm.common.ActiveMethod;
import com.sipcm.common.business.RoleService;
import com.sipcm.common.business.UserActivationService;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.Role;
import com.sipcm.common.model.User;
import com.sipcm.common.model.UserActivation;
import com.sipcm.email.EmailBean;
import com.sipcm.email.Emailer;
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

	public static final String USERNAME_PATTERN = "register.username.pattern";
	public static final String EMAIL_PATTERN = "register.email.pattern";
	public static final String ACTIVE_METHOD = "register.active.method";
	public static final String ACTIVE_EXPIRES = "register.active.expires";
	public static final String ADMIN_EMAIL = "global.admin.email";

	public static final String SELF_ACTIVE_EMAIL_TEMPATE = "/templates/self-active.vm";
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

	@ManagedProperty(value = "#{applicationConfiguration}")
	private Configuration appConfig;

	@ManagedProperty(value = "#{globalEmailer}")
	private Emailer emailer;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	@ManagedProperty(value = "#{roleService}")
	private RoleService roleService;

	@ManagedProperty(value = "#{userActivationService}")
	private UserActivationService userActivationService;

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
		usernamePattern = Pattern.compile(getUsernamePattern());
		emailPattern = Pattern.compile(getEmailPattern());
	}

	public String register() {
		String result;
		FacesContext context = FacesContext.getCurrentInstance();
		User user = userService.createNewEntity();
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
		if (ActiveMethod.NONE.equals(getActiveMethod())) {
			user.setStatus(AccountStatus.ACTIVE);
		}
		Role callerRole = roleService.getCallerRole();
		user.addRole(callerRole);
		userService.setPassword(user, password);
		userService.saveEntity(user);
		switch (getActiveMethod()) {
		case SELF:
			selfActive(user);
			break;
		case ADMIN:
			adminActive(user);
			break;
		default:
			break;
		}
		context.getExternalContext().getSessionMap().remove("registrationBean");
		result = "RegisterSuccess";
		return result;
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
		User user = userService.getUserByUsername(username);
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
		User user = userService.getUserByEmail(email);
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
	public void setAppConfig(Configuration appConfig) {
		this.appConfig = appConfig;
	}

	/**
	 * @param emailer
	 *            the emailer to set
	 */
	public void setEmailer(Emailer emailer) {
		this.emailer = emailer;
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
	 * @param userActivationService
	 *            the userActivationService to set
	 */
	public void setUserActivationService(
			UserActivationService userActivationService) {
		this.userActivationService = userActivationService;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username.trim();
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
		this.email = email.trim();
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
		this.password = password.trim();
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
		this.firstName = firstName.trim();
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
		this.middleName = (middleName == null || middleName.trim().isEmpty()) ? null
				: middleName.trim();
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
		this.lastName = lastName.trim();
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
		this.displayName = (displayName == null || displayName.trim().isEmpty()) ? null
				: displayName.trim();
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

	private String getUsernamePattern() {
		return appConfig.getString(USERNAME_PATTERN,
				"^\\p{Alpha}[\\w|\\.]{5,31}$");
	}

	private String getEmailPattern() {
		return appConfig.getString(EMAIL_PATTERN,
				"^[^@]+@[^@^\\.]+\\.[^@^\\.]+$");
	}

	/**
	 * Active method: None, Self or Admin
	 * 
	 * @return
	 */
	private ActiveMethod getActiveMethod() {
		String t = appConfig.getString(ACTIVE_METHOD, "SELF");
		try {
			return ActiveMethod.valueOf(t);
		} catch (Exception e) {
			return ActiveMethod.SELF;
		}
	}

	private String getAdminEmail() {
		return appConfig.getString(ADMIN_EMAIL);
	}

	private int getActiveExpires() {
		return appConfig.getInt(ACTIVE_EXPIRES, 72);
	}

	private void selfActive(User user) {
		UserActivation userActivation = userActivationService
				.createUserActivation(user, ActiveMethod.SELF,
						getActiveExpires());
		userActivationService.saveEntity(userActivation);
		EmailBean emailBean = new EmailBean();
		emailBean.addToAddress(user.getEmail());
		Map<String, Object> params = new HashMap<String, Object>();
		FacesContext ctx = FacesContext.getCurrentInstance();
		String scheme = ctx.getExternalContext().getRequestScheme();
		String sn = ctx.getExternalContext().getRequestServerName();
		int port = ctx.getExternalContext().getRequestServerPort();
		String serverUrl = scheme + "://" + sn;
		if ("HTTPS".equalsIgnoreCase(scheme)) {
			if (port != 443) {
				serverUrl = serverUrl + ":" + Integer.toString(port);
			}
		} else {
			if (port != 80) {
				serverUrl = serverUrl + ":" + Integer.toString(port);
			}
		}
		params.put("serverUrl", serverUrl);
		params.put("activation", userActivation);
		emailBean.setTemplate(SELF_ACTIVE_EMAIL_TEMPATE, params);
		emailBean.setParams(params);
		emailBean.setFromAddress(getAdminEmail());
		emailBean.setHtmlEncoded(true);
		emailBean.setSubject(Messages.getString(null,
				"register.active.self.email.subject", null));
		emailBean.setLocale(user.getLocale());
		emailer.sendMail(emailBean);
	}

	private void adminActive(User user) {
		UserActivation userActivation = userActivationService
				.createUserActivation(user, ActiveMethod.ADMIN,
						getActiveExpires());
		userActivationService.saveEntity(userActivation);
		if (getAdminEmail() != null) {
			prepareActiveEmail(userActivation, getAdminEmail());
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("System been configured to active account by admin, but there's not admin email setted.");
			}
		}
	}

	private void prepareActiveEmail(UserActivation userActivation, String email) {
		// EmailBean emailBean = new EmailBean();
		// emailBean.addToAddress(email);
		// // TODO:
		// emailer.sendMail(emailBean);
	}
}

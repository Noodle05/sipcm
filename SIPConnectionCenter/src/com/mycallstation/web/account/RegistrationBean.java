/**
 * 
 */
package com.mycallstation.web.account;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.common.AccountStatus;
import com.mycallstation.common.ActiveMethod;
import com.mycallstation.common.SystemConfiguration;
import com.mycallstation.common.business.RegistrationInvitationService;
import com.mycallstation.common.business.RoleService;
import com.mycallstation.common.business.UserActivationService;
import com.mycallstation.common.business.UserService;
import com.mycallstation.common.model.RegistrationInvitation;
import com.mycallstation.common.model.Role;
import com.mycallstation.common.model.User;
import com.mycallstation.common.model.UserActivation;
import com.mycallstation.web.util.EmailUtils;
import com.mycallstation.web.util.JSFUtils;
import com.mycallstation.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "registrationBean")
@RequestScoped
public class RegistrationBean implements Serializable {
	private static final long serialVersionUID = -6419289187735553748L;

	private static final Logger logger = LoggerFactory
			.getLogger(RegistrationBean.class);

	public static final String SELF_ACTIVE_EMAIL_TEMPLATE = "/templates/self-active.vm";
	public static final String ADMIN_ACTIVE_EMAIL_TEMPLATE = "/templates/admin-active.vm";

	@ManagedProperty("#{systemConfiguration}")
	private transient SystemConfiguration appConfig;

	@ManagedProperty("#{webEmailUtils}")
	private transient EmailUtils emailUtils;

	@ManagedProperty("#{userService}")
	private transient UserService userService;

	@ManagedProperty("#{roleService}")
	private transient RoleService roleService;

	@ManagedProperty("#{userActivationService}")
	private transient UserActivationService userActivationService;

	@ManagedProperty("#{registrationInvitationService}")
	private transient RegistrationInvitationService invitationService;

	private String username;

	private String email;

	private String password;

	private String firstName;

	private String middleName;

	private String lastName;

	private String displayName;

	private String locale;

	private String timeZone;

	private String invitationCode;

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
		RegistrationInvitation invitation = null;
		if (getAppConfig().isRegisterByInviteOnly()) {
			if (invitationCode == null) {
				FacesMessage message = Messages.getMessage(
						"register.error.by.invitation.only",
						FacesMessage.SEVERITY_ERROR);
				context.addMessage("registrationForm:invitationCode", message);
				return null;
			}
			invitation = getInvitationService().getInvitationByCode(
					invitationCode);
			if (invitation == null) {
				FacesMessage message = Messages.getMessage(
						"register.error.invalid.invitation.code",
						FacesMessage.SEVERITY_ERROR);
				context.addMessage("registrationForm:invitationCode", message);
				return null;
			}
			if (invitation.getExpireDate() != null
					&& invitation.getExpireDate().before(new Date())) {
				getInvitationService().removeEntity(invitation);
				FacesMessage message = Messages.getMessage(
						"register.error.invitation.code.expired",
						FacesMessage.SEVERITY_ERROR);
				context.addMessage("registrationForm:invitationCode", message);
				return null;
			}
			if (invitation.getCount() <= 0) {
				getInvitationService().removeEntity(invitation);
				FacesMessage message = Messages.getMessage(
						"register.error.invitation.code.all.taken",
						FacesMessage.SEVERITY_ERROR);
				context.addMessage("registrationForm:invitationCode", message);
				return null;
			}
			invitation.setCount(invitation.getCount() - 1);
		}
		User user = getUserService().createNewEntity();
		user.setUsername(username);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);
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
		if (ActiveMethod.NONE.equals(getAppConfig().getActiveMethod())) {
			user.setStatus(AccountStatus.ACTIVE);
		}
		Role userRole = getRoleService().getUserRole();
		user.addRole(userRole);
		getUserService().setPassword(user, password);
		getUserService().saveEntity(user);
		if (invitation != null) {
			if (invitation.getCount() <= 0) {
				getInvitationService().removeEntity(invitation);
			} else {
				getInvitationService().saveEntity(invitation);
			}
		}
		String message;
		switch (getAppConfig().getActiveMethod()) {
		case SELF:
			selfActive(user);
			message = "register.success.self.active";
			break;
		case ADMIN:
			adminActive(user);
			message = "register.success.admin.active";
			break;
		default:
			message = "register.success";
			break;
		}
		return "register-success.jsf?faces-redirect=true&messageId=" + message;
	}

	public void validateUsername(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String username = ((String) value).trim();
		if (username.length() < getAppConfig().getUsernameLengthMin()
				|| username.length() > getAppConfig().getUsernameLengthMax()) {
			FacesMessage message = Messages.getMessage(
					"register.error.username.length", new Object[] {
							getAppConfig().getUsernameLengthMin(),
							getAppConfig().getUsernameLengthMax() },
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		if (!usernamePattern.matcher(username).matches()) {
			FacesMessage message = Messages.getMessage(
					"register.error.username.pattern",
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		String[] blackList = appConfig.getUsernameBlackList();
		if (blackList != null) {
			for (String black : blackList) {
				if (username.toUpperCase().contains(black.toUpperCase())) {
					FacesMessage message = Messages.getMessage(
							"register.error.username.reserved",
							FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException(message);
				}
			}
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
	 * @param invitationService
	 *            the invitationService to set
	 */
	public void setInvitationService(
			RegistrationInvitationService invitationService) {
		this.invitationService = invitationService;
	}

	/**
	 * @return the invitationService
	 */
	private RegistrationInvitationService getInvitationService() {
		if (invitationService == null) {
			invitationService = JSFUtils.getManagedBean(
					"registrationInvitationService",
					RegistrationInvitationService.class);
		}
		return invitationService;
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

	/**
	 * @param invitationCode
	 *            the invitationCode to set
	 */
	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	/**
	 * @return the invitationCode
	 */
	public String getInvitationCode() {
		return invitationCode;
	}

	public List<String> getAvailableLocales() {
		return JSFUtils.getAvailableLocales();
	}

	public SelectItem[] getAvailableTimeZones() {
		return JSFUtils.getAvailableTimeZones();
	}

	private void selfActive(User user) {
		UserActivation userActivation = getUserActivationService()
				.createUserActivation(user, ActiveMethod.SELF,
						getAppConfig().getActiveExpires());
		getUserActivationService().saveEntity(userActivation);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activation", userActivation);
		params.put("activeExpires", getAppConfig().getActiveExpires());

		getEmailUtils().sendMail(
				user.getEmail(),
				user.getUserDisplayName(),
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
				getAppConfig().getAdminEmailPersonal(),
				Messages.getString(null, "register.active.admin.email.subject",
						null), ADMIN_ACTIVE_EMAIL_TEMPLATE, params,
				user.getLocale());
	}
}

/**
 * 
 */
package com.mycallstation.web.account;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import org.springframework.stereotype.Component;

import com.mycallstation.constant.AccountStatus;
import com.mycallstation.constant.ActiveMethod;
import com.mycallstation.dataaccess.business.RegistrationInvitationService;
import com.mycallstation.dataaccess.business.RoleService;
import com.mycallstation.dataaccess.business.UserActivationService;
import com.mycallstation.dataaccess.business.UserService;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.model.RegistrationInvitation;
import com.mycallstation.dataaccess.model.Role;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserActivation;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.scope.ViewScope;
import com.mycallstation.web.util.EmailUtils;
import com.mycallstation.web.util.JSFUtils;
import com.mycallstation.web.util.Messages;
import com.mycallstation.web.util.WebConfiguration;

/**
 * @author Wei Gao
 * 
 */
@Component("registrationBean")
@Scope(ViewScope.VIEW_SCOPE)
public class RegistrationBean implements Serializable {
	private static final long serialVersionUID = -6419289187735553748L;

	private static final Logger logger = LoggerFactory
			.getLogger(RegistrationBean.class);

	public static final String SELF_ACTIVE_EMAIL_TEMPLATE = "/templates/self-active.vm";
	public static final String ADMIN_ACTIVE_EMAIL_TEMPLATE = "/templates/admin-active.vm";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "registrationInvitationService")
	private RegistrationInvitationService registrationInvitationService;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "systemConfiguration")
	private WebConfiguration appConfig;

	@Resource(name = "roleService")
	private RoleService roleService;

	@Resource(name = "userActivationService")
	private UserActivationService userActivationService;

	@Resource(name = "webEmailUtils")
	private EmailUtils emailUtils;

	@Resource(name = "web.messages")
	private Messages messages;

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

	@PostConstruct
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("A new registration bean been created.");
		}
	}

	public String register() {
		FacesContext context = FacesContext.getCurrentInstance();
		RegistrationInvitation invitation = null;
		if (appConfig.isRegisterByInviteOnly()) {
			if (invitationCode == null) {
				FacesMessage message = messages.getMessage(
						"register.error.by.invitation.only",
						FacesMessage.SEVERITY_ERROR);
				context.addMessage("registrationForm:invitationCode", message);
				return null;
			}
			invitation = registrationInvitationService
					.getInvitationByCode(invitationCode);
			if (invitation == null) {
				FacesMessage message = messages.getMessage(
						"register.error.invalid.invitation.code",
						FacesMessage.SEVERITY_ERROR);
				context.addMessage("registrationForm:invitationCode", message);
				return null;
			}
			if (invitation.getExpireDate() != null
					&& invitation.getExpireDate().before(new Date())) {
				registrationInvitationService.removeEntity(invitation);
				FacesMessage message = messages.getMessage(
						"register.error.invitation.code.expired",
						FacesMessage.SEVERITY_ERROR);
				context.addMessage("registrationForm:invitationCode", message);
				return null;
			}
			if (invitation.getCount() <= 0) {
				registrationInvitationService.removeEntity(invitation);
				FacesMessage message = messages.getMessage(
						"register.error.invitation.code.all.taken",
						FacesMessage.SEVERITY_ERROR);
				context.addMessage("registrationForm:invitationCode", message);
				return null;
			}
			invitation.setCount(invitation.getCount() - 1);
		}
		User user = userService.createNewEntity();
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
		if (ActiveMethod.NONE.equals(appConfig.getActiveMethod())) {
			user.setStatus(AccountStatus.ACTIVE);
		}
		Role userRole = roleService.getUserRole();
		user.addRole(userRole);
		userService.setPassword(user, password);
		userService.saveEntity(user);
		UserSipProfile profile = userSipProfileService
				.createUserSipProfile(user);
		userSipProfileService.saveEntity(profile);
		if (invitation != null) {
			if (invitation.getCount() <= 0) {
				registrationInvitationService.removeEntity(invitation);
			} else {
				registrationInvitationService.saveEntity(invitation);
			}
		}
		String message;
		switch (appConfig.getActiveMethod()) {
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
		if (username.length() < appConfig.getUsernameLengthMin()
				|| username.length() > appConfig.getUsernameLengthMax()) {
			FacesMessage message = messages.getMessage(
					"register.error.username.length",
					new Object[] { appConfig.getUsernameLengthMin(),
							appConfig.getUsernameLengthMax() },
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		if (!getUsernamePattern().matcher(username).matches()) {
			FacesMessage message = messages.getMessage(
					"register.error.username.pattern",
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		String[] blackList = appConfig.getUsernameBlackList();
		if (blackList != null) {
			for (String black : blackList) {
				if (username.toUpperCase().contains(black.toUpperCase())) {
					FacesMessage message = messages.getMessage(
							"register.error.username.reserved",
							FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException(message);
				}
			}
		}
		User user = userService.getUserByUsername(username);
		if (user != null) {
			FacesMessage message = messages.getMessage(
					"register.error.username.exists",
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}

	public void validateEmail(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String email = ((String) value).trim();
		if (!getEmailPattern().matcher(email).matches()) {
			FacesMessage message = messages
					.getMessage("register.error.email.pattern",
							FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
		User user = userService.getUserByEmail(email);
		if (user != null) {
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
			String password = passwordTxt.getLocalValue().toString().trim();
			String confirmPasswd = confirmPasswdTxt.getLocalValue().toString()
					.trim();
			if (password != null && confirmPasswd != null) {
				if (!password.equals(confirmPasswd)) {
					FacesMessage message = messages.getMessage(
							"register.error.password.notmatch",
							FacesMessage.SEVERITY_ERROR);
					fc.addMessage("registrationForm:confirmPassword", message);
					fc.renderResponse();
				}
			}
		}
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

	private Pattern getUsernamePattern() {
		return Pattern.compile(appConfig.getUsernamePattern());
	}

	private Pattern getEmailPattern() {
		return Pattern.compile(appConfig.getEmailPattern());
	}

	private void selfActive(User user) {
		UserActivation userActivation = userActivationService
				.createUserActivation(user, ActiveMethod.SELF,
						appConfig.getActiveExpires());
		userActivationService.saveEntity(userActivation);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activation", userActivation);
		params.put("activeExpires", appConfig.getActiveExpires());

		emailUtils.sendMail(user.getEmail(), user.getUserDisplayName(),
				messages.getString(null, "register.active.self.email.subject",
						null), SELF_ACTIVE_EMAIL_TEMPLATE, params, user
						.getLocale());
	}

	private void adminActive(User user) {
		UserActivation userActivation = userActivationService
				.createUserActivation(user, ActiveMethod.ADMIN,
						appConfig.getActiveExpires());
		userActivationService.saveEntity(userActivation);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activation", userActivation);

		emailUtils.sendMail(appConfig.getAdminEmail(), appConfig
				.getAdminEmailPersonal(), messages.getString(null,
				"register.active.admin.email.subject", null),
				ADMIN_ACTIVE_EMAIL_TEMPLATE, params, user.getLocale());
	}
}

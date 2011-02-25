/**
 * 
 */
package com.sipcm.web.register;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;

import nl.captcha.Captcha;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.common.AccountStatus;
import com.sipcm.common.business.RoleService;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.Role;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "registrationBean")
@SessionScoped
public class RegistrationBean {
	private static final Logger logger = LoggerFactory
			.getLogger(RegistrationBean.class);

	public static final String USERNAME_PATTERN = "register.username.pattern";
	public static final String EMAIL_PATTERN = "register.email.pattern";
	public static final String ACTIVE_METHOD = "register.active.method";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "roleService")
	private RoleService roleService;

	@Resource(name = "applicationConfiguration")
	private Configuration appConfig;

	private String username;

	private String email;

	private String password;

	private String firstName;

	private String middleName;

	private String lastName;

	private String displayName;

	private ResourceBundle resource;

	private Pattern usernamePattern;

	private Pattern emailPattern;

	@PostConstruct
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("A new instance of Registration bean been created.");
		}
		resource = ResourceBundle.getBundle("messages.CallerPages");
		usernamePattern = Pattern.compile(getUsernamePattern());
		emailPattern = Pattern.compile(getEmailPattern());
	}

	public String register() {
		String result;
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove(Captcha.NAME);
		User user = userService.createNewEntity();
		user.setUsername(username);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);
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
		result = "RegisterSuccess";
		return result;
	}

	public void validateUsername(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String username = ((String) value).trim();
		if (!usernamePattern.matcher(username).matches()) {
			FacesMessage message = new FacesMessage(
					resource.getString("register.error.username.pattern"));
			throw new ValidatorException(message);
		}
		User user = userService.getUserByUsername(username);
		if (user != null) {
			FacesMessage message = new FacesMessage(
					resource.getString("register.error.username.exists"));
			throw new ValidatorException(message);
		}
	}

	public void validateEmail(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String email = ((String) value).trim();
		if (!emailPattern.matcher(email).matches()) {
			FacesMessage message = new FacesMessage(
					resource.getString("register.error.email.pattern"));
			throw new ValidatorException(message);
		}
		User user = userService.getUserByEmail(email);
		if (user != null) {
			FacesMessage message = new FacesMessage(
					resource.getString("register.error.email.exists"));
			throw new ValidatorException(message);
		}
	}

	public void validateCaptcha(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String text = ((String) value).trim();
		Captcha captcha = (Captcha) context.getExternalContext()
				.getSessionMap().get(Captcha.NAME);
		if (captcha == null || !captcha.isCorrect(text)) {
			FacesMessage message = new FacesMessage(
					resource.getString("register.error.captcha.notmatch"));
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
					FacesMessage message = new FacesMessage(
							resource.getString("register.error.password.notmatch"));
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
		String t = appConfig.getString(ACTIVE_METHOD, "self");
		try {
			return ActiveMethod.valueOf(t);
		} catch (Exception e) {
			return ActiveMethod.SELF;
		}
	}

	private void selfActive(User user) {
		// TODO:
	}

	private void adminActive(User user) {
		// TODO:
	}
}

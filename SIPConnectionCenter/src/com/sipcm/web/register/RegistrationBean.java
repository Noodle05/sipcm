/**
 * 
 */
package com.sipcm.web.register;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import nl.captcha.Captcha;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.common.business.RoleService;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.Role;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@Component("registrationBean")
@Scope("request")
public class RegistrationBean {
	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "roleService")
	private RoleService roleService;

	private String username;

	private String email;

	private String password;

	private String confirmPassword;

	private String firstName;

	private String middleName;

	private String lastName;

	private String displayName;

	private String captchaCode;

	private ResourceBundle resource;

	@PostConstruct
	public void init() {
		resource = ResourceBundle.getBundle("messages.CallerPages");
	}

	public String register() {
		String result = "Register";
		FacesContext context = FacesContext.getCurrentInstance();
		Captcha captcha = (Captcha) context.getExternalContext()
				.getSessionMap().get(Captcha.NAME);
		if (captcha == null || !captcha.isCorrect(captchaCode)) {
			FacesMessage message = new FacesMessage(
					resource.getString("register.error.captcha.notmatch"));
			context.addMessage("registrationForm:captchaCode", message);
			return result;
		}
		context.getExternalContext().getSessionMap().remove(Captcha.NAME);
		if (!password.equals(confirmPassword)) {
			FacesMessage message = new FacesMessage(
					resource.getString("register.error.password.notmatch"));
			context.addMessage("registrationForm:confirmPassword", message);
			return result;
		}
		User user = userService.createNewEntity();
		user.setUsername(username);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);
		Role callerRole = roleService.getCallerRole();
		user.addRole(callerRole);
		userService.setPassword(user, password);
		userService.saveEntity(user);
		result = "RegisterSuccess";
		return result;
	}

	public void validateUsername(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String username = (String) value;
		if (!Pattern.matches("^\\p{Alpha}[\\w|\\.]{5,31}$", username)) {
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
		String email = (String) value;
		if (!Pattern.matches("^[^@]+@[^@^\\.]+\\.[^@^\\.]+$", email)) {
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
	 * @param confirmPassword
	 *            the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
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
	 * @param captchaCode
	 *            the captchaCode to set
	 */
	public void setCaptchaCode(String captchaCode) {
		this.captchaCode = captchaCode;
	}

	/**
	 * @return the captchaCode
	 */
	public String getCaptchaCode() {
		return captchaCode;
	}
}

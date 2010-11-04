/**
 * 
 */
package com.sipcm.web.register;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.common.business.UserService;
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

	// @NotNull(message = "{register.error.username.required}")
	// @Pattern(regexp = "^\\p{Alpha}[\\w|\\.]{5,31}$", message =
	// "{register.error.username.pattern}")
	private String username;

	// @NotNull(message = "{register.error.email.required}")
	// @Pattern(regexp = "^[^@]+@[^@^\\.]+\\.[^@^\\.]+$", message =
	// "{register.error.email.pattern}")
	private String email;

	// @NotNull(message = "{register.error.password.required}")
	// @Size(min = 6, max = 64, message = "{register.error.password.size}")
	private String password;

	// @NotNull(message = "{register.error.firstName.required}")
	private String firstName;

	private String middleName;

	// @NotNull(message = "{register.error.lastName.required}")
	private String lastName;

	private String displayName;

	// @NotNull(message = "{register.error.phoneNumber.required}")
	// @Pattern(regexp =
	// "^\\s*((\\+|00|011)?[1-9]\\d*\\s*)?(\\([1-9]\\d*\\))?\\s*[1-9]\\d*(\\s*-?\\d+)+\\s*$",
	// message = "{register.error.phoneNumber.pattern}")
	private String phoneNumber;

	// @NotNull(message = "{register.error.defaultAreaCode.required}")
	// @Pattern(regexp = "^\\d{3,}$", message =
	// "{register.error.defaultAreaCode.pattern}")
	private String defaultAreaCode;

	public String register() {
		User user = userService.createNewEntity();
		user.setUsername(username);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);
		user.setPhoneNumber(phoneNumber);
		user.setDefaultAreaCode(defaultAreaCode);
		userService.setPassword(user, password);
		userService.saveEntity(user);
		return "RegisterSuccess";
	}

	public void validateUsername(FacesContext context,
			UIComponent componentToValidate, Object value) {
		ResourceBundle resource = ResourceBundle
				.getBundle("messages.CallerPages");
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
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param defaultAreaCode
	 *            the defaultAreaCode to set
	 */
	public void setDefaultAreaCode(String defaultAreaCode) {
		this.defaultAreaCode = defaultAreaCode;
	}

	/**
	 * @return the defaultAreaCode
	 */
	public String getDefaultAreaCode() {
		return defaultAreaCode;
	}
}

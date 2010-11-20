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

import com.sipcm.common.business.RoleService;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.Role;
import com.sipcm.common.model.User;
import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Component("registrationBean")
@Scope("request")
public class RegistrationBean {
	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "roleService")
	private RoleService roleService;

	private String username;

	private String email;

	private String password;

	private String firstName;

	private String middleName;

	private String lastName;

	private String displayName;

	private String phoneNumber;

	private String defaultAreaCode;

	public String register() {
		User user = userService.createNewEntity();
		user.setUsername(username);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);
		Role callerRole = roleService.getCallerRole();
		user.addRole(callerRole);
		UserSipProfile sipProfile = userSipProfileService
				.createUserSipProfile(user);
		sipProfile.setPhoneNumber(phoneNumber);
		sipProfile.setDefaultAreaCode(defaultAreaCode);
		userService.setPassword(user, password);
		userService.saveEntity(user);
		userSipProfileService.saveEntity(sipProfile);
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
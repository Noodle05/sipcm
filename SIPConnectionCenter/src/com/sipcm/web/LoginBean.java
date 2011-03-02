/**
 * 
 */
package com.sipcm.web;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;

import com.sipcm.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "loginBean")
@RequestScoped
public class LoginBean implements Serializable {
	private static final long serialVersionUID = 8509736904609684199L;

	private String username = "";
	private String password = "";
	private boolean rememberMe = false;
	private boolean loggedIn = false;

	// This is the action method called when the user clicks the "login" button
	public String doLogin() throws IOException, ServletException {
		ExternalContext context = FacesContext.getCurrentInstance()
				.getExternalContext();

		RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
				.getRequestDispatcher("/j_spring_security_check");

		dispatcher.forward((ServletRequest) context.getRequest(),
				(ServletResponse) context.getResponse());

		FacesContext.getCurrentInstance().responseComplete();
		// It's OK to return null here because Faces is just going to exit.
		return null;
	}

	@PostConstruct
	public void handleErrorMessage() {
		Exception e = (Exception) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap()
				.get(WebAttributes.AUTHENTICATION_EXCEPTION);

		if (e instanceof BadCredentialsException) {
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap()
					.remove(WebAttributes.AUTHENTICATION_EXCEPTION);
			FacesContext.getCurrentInstance().addMessage(
					null,
					Messages.getMessage(
							"login.error.username.password.invalid",
							FacesMessage.SEVERITY_ERROR));
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(final boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(final boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
}

/**
 * 
 */
package com.sipcm.googlevoice;

/**
 * @author wgao
 * 
 */
public enum AuthenticationErrorCode {
	BadAuthentication("Wrong username or password."), NotVerified(
			"The account email address has not been verified. You need to access your Google account directly to resolve the issue."), TermsNotAgreed(
			"You have not agreed to terms. You need to access your Google account directly to resolve the issue."), CaptchaRequired(
			"A CAPTCHA is required. (A response with this error code will also contain an image URL and a CAPTCHA token.)"), Unknown(
			"Unknown or unspecified error; the request contained invalid input or was malformed."), AccountDeleted(
			"The user account has been deleted."), AccountDisabled(
			"The user account has been disabled."), ServiceDisabled(
			"Your access to the voice service has been disabled. (Your user account may still be valid.)"), ServiceUnavailable(
			"The service is not available; try again later.");
	private final String message;

	private AuthenticationErrorCode(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

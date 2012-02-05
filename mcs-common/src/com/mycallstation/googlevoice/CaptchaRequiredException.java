/**
 * 
 */
package com.mycallstation.googlevoice;

/**
 * @author Wei Gao
 * 
 */
public class CaptchaRequiredException extends GoogleAuthenticationException {
	private static final long serialVersionUID = -239810563211939082L;

	private String captchaToken;
	private String captchaUrl;

	CaptchaRequiredException(String captchaToken, String captchaUrl) {
		super(AuthenticationErrorCode.CaptchaRequired);
		this.captchaToken = captchaToken;
		this.captchaUrl = captchaUrl;
	}

	public String getCaptchaToken() {
		return captchaToken;
	}

	public String getCaptchaUrl() {
		return captchaUrl;
	}
}

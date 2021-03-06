/**
 * 
 */
package com.mycallstation.external.recaptcha;

/**
 * @author Wei Gao
 * 
 */
public abstract class GoogleRecaptchaManager {
	protected abstract GoogleRecaptcha createGoogleRecaptcha();

	public GoogleRecaptcha getRecaptcha(String recaptchaKey) {
		GoogleRecaptcha recaptcha = createGoogleRecaptcha();
		recaptcha.setKey(recaptchaKey);
		return recaptcha;
	}
}

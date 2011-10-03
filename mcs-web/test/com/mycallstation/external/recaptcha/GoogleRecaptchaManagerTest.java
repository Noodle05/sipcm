package com.mycallstation.external.recaptcha;

public class GoogleRecaptchaManagerTest extends GoogleRecaptchaManager {
	@Override
	protected GoogleRecaptcha createGoogleRecaptcha() {
		return new GoogleRecaptcha();
	}
}

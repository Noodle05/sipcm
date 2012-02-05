/**
 * 
 */
package com.mycallstation.googlevoice;

import com.mycallstation.common.AuthenticationException;

/**
 * @author Wei Gao
 * 
 */
public class GoogleAuthenticationException extends AuthenticationException {
	private static final long serialVersionUID = -1399324412249638865L;

	protected AuthenticationErrorCode errorCode;

	GoogleAuthenticationException(AuthenticationErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public static void throwProperException(AuthenticationErrorCode errorCode,
			String captchaToken, String captchaUrl)
			throws GoogleAuthenticationException {
		switch (errorCode) {
		case AccountDeleted:
			throw new AccountDeletedException();
		case AccountDisabled:
			throw new AccountDisabledException();
		case BadAuthentication:
			throw new BadAuthenticationException();
		case CaptchaRequired:
			throw new CaptchaRequiredException(captchaToken, captchaUrl);
		case NotVerified:
			throw new NotVerifiedException();
		case ServiceDisabled:
			throw new ServiceDisabledException();
		case ServiceUnavailable:
			throw new ServiceUnavailableException();
		case TermsNotAgreed:
			throw new TermsNotAgreedException();
		default:
			throw new GoogleAuthenticationException(errorCode);
		}
	}

	public AuthenticationErrorCode getError() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return errorCode.getMessage();
	}
}

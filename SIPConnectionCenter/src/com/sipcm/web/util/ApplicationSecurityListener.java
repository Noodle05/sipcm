/**
 * 
 */
package com.sipcm.web.util;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.sipcm.common.model.User;
import com.sipcm.security.UserDetailsImpl;
import com.sipcm.web.LocaleTimeZoneHolderBean;

/**
 * @author wgao
 * 
 */
@Component("applicationSecurityListener")
public class ApplicationSecurityListener implements
		ApplicationListener<AuthenticationSuccessEvent> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org
	 * .springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		Authentication auth = event.getAuthentication();
		Object principal = auth.getPrincipal();
		Locale locale;
		TimeZone timeZone;
		if (principal != null) {
			if (principal instanceof UserDetailsImpl) {
				User user = ((UserDetailsImpl) principal).getUser();
				locale = user.getLocale();
				timeZone = user.getTimeZone();
				if (locale != null || timeZone != null) {
					LocaleTimeZoneHolderBean bean = JSFUtils.getManagedBean(
							"localeTimeZoneHolderBean",
							LocaleTimeZoneHolderBean.class);
					if (bean != null) {
						if (locale != null && !bean.getLocale().equals(locale)) {
							bean.setLocale(locale);
						}
						if (timeZone != null
								&& !bean.getTimeZone().equals(timeZone)) {
							bean.setTimeZone(timeZone);
						}
					}
				}
			}
		}
	}
}

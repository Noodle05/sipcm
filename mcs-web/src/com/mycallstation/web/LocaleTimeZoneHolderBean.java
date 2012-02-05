/**
 * 
 */
package com.mycallstation.web;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.mycallstation.dataaccess.model.User;
import com.mycallstation.web.util.JSFUtils;

/**
 * @author Wei Gao
 * 
 */
@Component("localeTimeZoneHolderBean")
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LocaleTimeZoneHolderBean implements Serializable {
	private static final long serialVersionUID = 3748969759027996323L;

	@Resource(name = "jsfUtils")
	private JSFUtils jsfUtils;

	private Locale locale;
	private TimeZone timeZone;

	@PostConstruct
	public void init() {
		User user = jsfUtils.getCurrentUser();
		if (user != null) {
			setLocale(user.getLocale());
			setTimeZone(user.getTimeZone());
		} else {
			setLocale(null);
			setTimeZone(null);
		}
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale) {
		if (locale != null) {
			this.locale = locale;
		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			if (context != null) {
				ExternalContext c = context.getExternalContext();
				if (c != null && c.getRequestLocale() != null) {
					this.locale = c.getRequestLocale();
				}
			}
			if (this.locale == null) {
				this.locale = Locale.getDefault();
			}
		}
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param timeZone
	 *            the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		if (timeZone != null) {
			this.timeZone = timeZone;
		} else {
			this.timeZone = TimeZone.getDefault();
		}
	}

	/**
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}
}

/**
 * 
 */
package com.sipcm.web;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.sipcm.common.model.User;
import com.sipcm.web.util.JSFUtils;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "localeTimeZoneHolderBean")
@SessionScoped
public class LocaleTimeZoneHolderBean implements Serializable {
	private static final long serialVersionUID = 3748969759027996323L;

	private Locale locale;
	private TimeZone timeZone;
	private transient User user;

	@PostConstruct
	public void init() {
		user = JSFUtils.getCurrentUser();
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

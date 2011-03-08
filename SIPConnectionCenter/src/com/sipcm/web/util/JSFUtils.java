/**
 * 
 */
package com.sipcm.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.security.UserDetailsImpl;
import com.sipcm.web.LocaleTimeZoneHolderBean;

/**
 * @author wgao
 * 
 */
public abstract class JSFUtils {
	public static final String NA = "---";

	public static final String[] availableTimeZones = new String[] { NA,
			"Pacific/Midway", "US/Hawaii", "US/Alaska", "US/Pacific",
			"America/Tijuana", "US/Arizona", "America/Chihuahua",
			"US/Mountain", "America/Guatemala", "US/Central",
			"America/Mexico_City", "Canada/Saskatchewan", "America/Bogota",
			"US/Eastern", "US/East-Indiana", "Canada/Eastern",
			"America/Caracas", "America/Manaus", "America/Santiago",
			"Canada/Newfoundland", "Brazil/East", "America/Buenos_Aires",
			"America/Godthab", "America/Montevideo", "Atlantic/South_Georgia",
			"Atlantic/Azores", "Atlantic/Cape_Verde", "Africa/Casablanca",
			"Europe/London", "Europe/Berlin", "Europe/Belgrade",
			"Europe/Brussels", "Europe/Warsaw", "Africa/Algiers", "Asia/Amman",
			"Europe/Athens", "Asia/Beirut", "Africa/Cairo", "Africa/Harare",
			"Europe/Helsinki", "Asia/Jerusalem", "Europe/Minsk",
			"Africa/Windhoek", "Asia/Baghdad", "Asia/Kuwait", "Europe/Moscow",
			"Africa/Nairobi", "Asia/Tbilisi", "Asia/Tehran", "Asia/Muscat",
			"Asia/Baku", "Asia/Yerevan", "Asia/Kabul", "Asia/Yekaterinburg",
			"Asia/Karachi", "Asia/Calcutta", "Asia/Colombo", "Asia/Katmandu",
			"Asia/Novosibirsk", "Asia/Dhaka", "Asia/Rangoon", "Asia/Bangkok",
			"Asia/Krasnoyarsk", "Asia/Hong_Kong", "Asia/Irkutsk",
			"Asia/Kuala_Lumpur", "Australia/Perth", "Asia/Taipei",
			"Asia/Tokyo", "Asia/Seoul", "Asia/Yakutsk", "Australia/Adelaide",
			"Australia/Darwin", "Australia/Brisbane", "Australia/Sydney",
			"Pacific/Guam", "Australia/Hobart", "Asia/Vladivostok",
			"Asia/Magadan", "Pacific/Auckland", "Pacific/Fiji",
			"Pacific/Tongatapu" };
	static {
		Arrays.sort(availableTimeZones);
	}

	public static final Map<String, Locale> availableLocales = new LinkedHashMap<String, Locale>();
	static {
		availableLocales.put(NA, null);
		availableLocales.put(Locale.US.getDisplayCountry(Locale.US), Locale.US);
		availableLocales.put(Locale.CHINA.getDisplayCountry(Locale.CHINA),
				Locale.CHINA);
	}

	public static <T> T getManagedBean(String managedBeanKey, Class<T> clazz)
			throws IllegalArgumentException {
		if (managedBeanKey == null) {
			throw new NullPointerException("Managed Bean Key is null.");
		}
		if (managedBeanKey.isEmpty()) {
			throw new IllegalArgumentException("Managed Bean key is empty.");
		}
		if (clazz == null) {
			throw new NullPointerException("Class is null.");
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null) {
			return null;
		}
		ELResolver resolver = facesContext.getApplication().getELResolver();
		ELContext elContext = facesContext.getELContext();
		Object managedBean = resolver.getValue(elContext, null, managedBeanKey);
		if (!elContext.isPropertyResolved()) {
			throw new IllegalArgumentException(
					"No managed bean found for key: " + managedBeanKey);
		}
		if (managedBean == null) {
			return null;
		} else {
			if (clazz.isInstance(managedBean)) {
				return clazz.cast(managedBean);
			} else {
				throw new IllegalArgumentException(
						"Managed bean is not of type [" + clazz.getName()
								+ "] | Actual type is: ["
								+ managedBean.getClass().getName() + "]");
			}
		}
	}

	public static User getCurrentUser() {
		User user = null;
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			Authentication auth = context.getAuthentication();
			if (auth != null) {
				Object principal = auth.getPrincipal();
				if (principal != null) {
					if (principal instanceof UserDetailsImpl) {
						user = ((UserDetailsImpl) principal).getUser();
					} else {
						String username = principal.toString();
						UserService userService = getManagedBean("userService",
								UserService.class);
						user = userService.getUserByUsername(username);
					}
				}
			}
		}
		return user;
	}

	public static Locale getCurrentLocale() {
		Locale locale = null;
		LocaleTimeZoneHolderBean b = getManagedBean("localeTimeZoneHolderBean",
				LocaleTimeZoneHolderBean.class);
		if (b != null) {
			locale = b.getLocale();
		}
		if (locale == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			if (context != null) {
				ExternalContext c = context.getExternalContext();
				if (c != null && c.getRequestLocale() != null) {
					locale = c.getRequestLocale();
				}
			}
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}

	public static TimeZone getCurrentTimeZone() {
		TimeZone timeZone = null;
		LocaleTimeZoneHolderBean b = getManagedBean("localeTimeZoneHolderBean",
				LocaleTimeZoneHolderBean.class);
		if (b != null) {
			timeZone = b.getTimeZone();
		}
		if (timeZone == null) {
			timeZone = TimeZone.getDefault();
		}
		return timeZone;
	}

	public static List<String> getAvailableLocales() {
		return new ArrayList<String>(availableLocales.keySet());
	}

	public static String[] getAvailableTimeZones() {
		return availableTimeZones;
	}
}

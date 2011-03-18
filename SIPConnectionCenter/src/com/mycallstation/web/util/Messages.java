/**
 * 
 */
package com.mycallstation.web.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

/**
 * @author wgao
 * 
 */
public abstract class Messages {
	public static FacesMessage getMessage(String resourceId) {
		return getMessage(null, resourceId, null, null);
	}

	public static FacesMessage getMessage(String resourceId, Severity severity) {
		return getMessage(null, resourceId, null, severity);
	}

	public static FacesMessage getMessage(String resourceId, Object[] params) {
		return getMessage(null, resourceId, params, null);
	}

	public static FacesMessage getMessage(String resourceId, Object[] params,
			Severity severity) {
		return getMessage(null, resourceId, params, severity);
	}

	public static FacesMessage getMessage(String bundleName, String resourceId,
			Object[] params) {
		return getMessage(bundleName, resourceId, params, null);
	}

	public static FacesMessage getMessage(String bundleName, String resourceId,
			Object[] params, FacesMessage.Severity severity) {
		Locale locale = JSFUtils.getCurrentLocale();
		ClassLoader loader = getClassLoader();
		String summary = getString(bundleName, resourceId, locale, loader,
				params);
		if (summary == null) {
			summary = "???" + resourceId + "???";
		}
		String detail = getString(bundleName, resourceId + "_detail", locale,
				loader, params);
		if (severity != null) {
			return new FacesMessage(severity, summary, detail);
		} else {
			return new FacesMessage(summary, detail);
		}
	}

	public static String getString(String bundle, String resourceId,
			Object[] params) {
		Locale locale = JSFUtils.getCurrentLocale();
		ClassLoader loader = getClassLoader();
		return getString(bundle, resourceId, locale, loader, params);
	}

	public static String getString(String bundleStr, String resourceId,
			Locale locale, ClassLoader loader, Object[] params) {
		String resource = null;
		ResourceBundle bundle;

		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		bundle = app.getResourceBundle(context, "messages");

		if (bundle != null) {
			try {
				resource = bundle.getString(resourceId);
			} catch (MissingResourceException ex) {
			}
		}

		if (resource == null && bundleStr != null) {
			bundle = ResourceBundle.getBundle(bundleStr, locale, loader);
			if (bundle != null) {
				try {
					resource = bundle.getString(resourceId);
				} catch (MissingResourceException ex) {
				}
			}
		}
		if (resource == null) {
			return null;
		}
		if (params == null) {
			return resource;
		}
		MessageFormat formatter = new MessageFormat(resource);
		return formatter.format(params);
	}

	public static ClassLoader getClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		return loader;
	}
}

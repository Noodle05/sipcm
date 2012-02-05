/**
 * 
 */
package com.mycallstation.web.util;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.mycallstation.email.sender.EmailBean;
import com.mycallstation.email.sender.Emailer;

/**
 * @author Wei Gao
 * 
 */
@Component("webEmailUtils")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EmailUtils {
	private volatile String serverUrl;

	@Resource(name = "systemConfiguration")
	private WebConfiguration appConfig;

	@Resource(name = "globalEmailer")
	private Emailer emailer;

	public void sendMail(String target, String targetPersonal, String subject,
			String template, Map<String, Object> params, Locale locale) {
		EmailBean emailBean = new EmailBean();
		emailBean.addToAddress(target, targetPersonal);
		params.put("serverUrl", getServerUrl());
		emailBean.setTemplate(template, params);
		emailBean.setParams(params);
		emailBean.setFromAddress(appConfig.getFromEmail(),
				appConfig.getFromEmailPersonal());
		emailBean.setHtmlEncoded(true);
		emailBean.setSubject(subject);
		emailBean.setLocale(locale);
		emailer.sendMail(emailBean);
	}

	private String getServerUrl() {
		if (serverUrl == null) {
			synchronized (this) {
				if (serverUrl == null) {
					FacesContext ctx = FacesContext.getCurrentInstance();
					String scheme = ctx.getExternalContext().getRequestScheme();
					String sn = ctx.getExternalContext().getRequestServerName();
					int port = ctx.getExternalContext().getRequestServerPort();
					serverUrl = scheme + "://" + sn;
					if ("HTTPS".equalsIgnoreCase(scheme)) {
						if (port != 443) {
							serverUrl = serverUrl + ":"
									+ Integer.toString(port);
						}
					} else {
						if (port != 80) {
							serverUrl = serverUrl + ":"
									+ Integer.toString(port);
						}
					}
				}
			}
		}
		return serverUrl;
	}
}

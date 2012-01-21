/**
 * 
 */
package com.mycallstation.web.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("durationConverter")
public class DurationConverter implements Converter {
	private static final Pattern pattern = Pattern
			.compile("^(\\d{2,}):(\\d{2}):(\\d{2})$");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (value == null) {
			return null;
		}
		Matcher m = pattern.matcher(value.trim());
		if (m.matches()) {
			int h = Integer.parseInt(m.group(1));
			int mi = Integer.parseInt(m.group(2));
			int s = Integer.parseInt(m.group(3));
			long ret = ((long) (h * 3600 + mi * 60 + s)) * 1000L;
			return ret;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Long) {
			long l = (Long) value;
			int t = (int) (l / 1000L) + (l % 1000L > 0 ? 1 : 0);
			int s = t % 60;
			t = t / 60;
			int mi = t % 60;
			t = t / 60;
			StringBuilder sb = new StringBuilder();
			sb.append(t).append(":").append(mi < 10 ? "0" : "").append(mi)
					.append(":").append(s < 10 ? "0" : "").append(s);
			return sb.toString();
		} else {
			return value.toString();
		}
	}
}

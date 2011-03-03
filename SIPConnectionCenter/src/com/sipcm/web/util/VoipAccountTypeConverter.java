/**
 * 
 */
package com.sipcm.web.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

import com.sipcm.sip.VoipAccountType;

/**
 * @author wgao
 * 
 */
@Component("voipAccountTypeConverter")
public class VoipAccountTypeConverter implements Converter {
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
		} else {
			Integer o = Integer.parseInt(value);
			return VoipAccountType.values()[(o - 1)];
		}
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
		} else {
			return Integer.toString(((VoipAccountType) value).ordinal() + 1);
		}
	}
}

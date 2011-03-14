/**
 * 
 */
package com.mycallstation.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

import com.mycallstation.sip.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
@Component("naPhoneNumberConverter")
public class NAPhoneNumberConverter implements Converter {

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
			return PhoneNumberUtil.getCanonicalizedPhoneNumber(value);
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
			return PhoneNumberUtil.formattedNAPhoneNumber((String) value);
		}
	}
}

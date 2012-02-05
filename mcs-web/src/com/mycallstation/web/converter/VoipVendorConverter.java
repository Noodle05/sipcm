/**
 * 
 */
package com.mycallstation.web.converter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.business.VoipVendorService;
import com.mycallstation.dataaccess.model.VoipVendor;

/**
 * @author Wei Gao
 * 
 */
@Component("voipVendorConverter")
public class VoipVendorConverter implements Converter {
	@Resource(name = "voipVendorService")
	private VoipVendorService voipVendorService;

	private final Map<Integer, VoipVendor> cache;

	public VoipVendorConverter() {
		cache = new HashMap<Integer, VoipVendor>();
	}

	@PostConstruct
	public void init() {
		Collection<VoipVendor> vs = voipVendorService.getManagableVoipVendors();
		for (VoipVendor v : vs) {
			cache.put(v.getId(), v);
		}
	}

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
			Integer id = Integer.parseInt(value);
			return cache.get(id);
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
			return ((VoipVendor) value).getId().toString();
		}
	}
}

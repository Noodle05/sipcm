/**
 * 
 */
package com.sipcm.sip.util;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.sip.VoipVendorType;

/**
 * @author wgao
 * 
 */
@Component("mapHolderBean")
public class MapHolderBean {

	@Resource(name = "voipVendorToServletMap")
	private Map<VoipVendorType, String> voipVendorToServletMap;

	public EnumMap<VoipVendorType, String> getVoipVendorToServletMap() {
		return new EnumMap<VoipVendorType, String>(voipVendorToServletMap);
	}
}

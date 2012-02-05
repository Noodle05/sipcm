/**
 * 
 */
package com.mycallstation.sip.util;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mycallstation.constant.VoipVendorType;

/**
 * @author Wei Gao
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

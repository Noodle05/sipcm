/**
 * 
 */
package com.sipcm.sip.business;

import java.util.Collection;

import com.sipcm.base.business.Service;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
public interface VoipVendorService extends Service<VoipVendor, Integer> {
	public Collection<VoipVendor> getManagableVoipVendors();

	public VoipVendor getGoogleVoiceVendor();

	public Collection<VoipVendor> getSIPVendors();
}

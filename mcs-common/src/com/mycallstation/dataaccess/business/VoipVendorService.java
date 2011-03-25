/**
 * 
 */
package com.mycallstation.dataaccess.business;

import java.util.Collection;

import com.mycallstation.base.business.Service;
import com.mycallstation.dataaccess.model.VoipVendor;

/**
 * @author wgao
 * 
 */
public interface VoipVendorService extends Service<VoipVendor, Integer> {
	public Collection<VoipVendor> getManagableVoipVendors();

	public VoipVendor getGoogleVoiceVendor();

	public Collection<VoipVendor> getSIPVendors();
}

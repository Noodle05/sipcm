/**
 * 
 */
package com.sipcm.sip;

import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
public interface VoipVendorContext {
	public void setVoipVendor(VoipVendor voipVendor);

	public void init() throws Exception;
}

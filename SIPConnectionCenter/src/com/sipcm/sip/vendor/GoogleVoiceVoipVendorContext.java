/**
 * 
 */
package com.sipcm.sip.vendor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Component("googleVoiceVoipVendorContext")
@Scope("prototype")
public class GoogleVoiceVoipVendorContext implements VoipVendorContext {
	private VoipVendor voipVendor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.VoipVenderContext#setVoipVendor(com.sipcm.sip.model.VoipVendor
	 * )
	 */
	@Override
	public void setVoipVendor(VoipVendor voipVendor) {
		this.voipVendor = voipVendor;
	}

	/**
	 * @return the voipVendor
	 */
	public VoipVendor getVoipVendor() {
		return voipVendor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.VoipVendorContext#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}

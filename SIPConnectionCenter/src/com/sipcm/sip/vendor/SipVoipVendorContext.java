/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.sip.IpPort;
import com.sipcm.sip.Protocol;
import com.sipcm.sip.model.VoipVendor;
import com.sipcm.util.DnsUtil;

/**
 * @author wgao
 * 
 */
@Component("sipVoipVendorContext")
@Scope("prototype")
public class SipVoipVendorContext implements VoipVendorContext {
	private VoipVendor voipVendor;

	@Resource(name = "dnsUtil")
	private DnsUtil dnsUtil;

	private Map<Protocol, List<IpPort>> connectionInfo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.VoipVenderContext#init()
	 */
	@Override
	public void init() throws Exception {
		connectionInfo = dnsUtil.lookupSipSrv(voipVendor.getDomain());
	}

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
	 * @return the voipVender
	 */
	public VoipVendor getVoipVendor() {
		return voipVendor;
	}
}

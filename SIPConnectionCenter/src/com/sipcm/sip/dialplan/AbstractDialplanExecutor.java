/**
 * 
 */
package com.sipcm.sip.dialplan;

import javax.annotation.Resource;

import com.sipcm.sip.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
public abstract class AbstractDialplanExecutor implements DialplanExecutor {
	@Resource(name = "phoneNumberUtil")
	protected PhoneNumberUtil phoneNumberUtil;
}

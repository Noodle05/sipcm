/**
 * 
 */
package com.sipcm.common.business;

import com.sipcm.base.business.Service;
import com.sipcm.common.model.RegistrationInvitation;

/**
 * @author wgao
 * 
 */
public interface RegistrationInvitationService extends
		Service<RegistrationInvitation, Integer> {
	public RegistrationInvitation generateInvitation(int count, int days);

	public RegistrationInvitation getInvitationByCode(String code);
}

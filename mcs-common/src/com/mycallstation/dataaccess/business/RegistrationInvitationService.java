/**
 * 
 */
package com.mycallstation.dataaccess.business;

import com.mycallstation.base.business.Service;
import com.mycallstation.dataaccess.model.RegistrationInvitation;

/**
 * @author wgao
 * 
 */
public interface RegistrationInvitationService extends
		Service<RegistrationInvitation, Integer> {
	public RegistrationInvitation generateInvitation(int count, int days);

	public RegistrationInvitation getInvitationByCode(String code);
}

/**
 * 
 */
package com.sipcm.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.common.dao.RegistrationInvitationDAO;
import com.sipcm.common.model.RegistrationInvitation;

/**
 * @author wgao
 * 
 */
@Repository("registrationInvitationDAO")
public class RegistrationInvitationDAOImpl extends
		AbstractDAO<RegistrationInvitation, Integer> implements
		RegistrationInvitationDAO {
}

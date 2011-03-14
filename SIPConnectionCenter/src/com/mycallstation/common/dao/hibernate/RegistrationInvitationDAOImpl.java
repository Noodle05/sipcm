/**
 * 
 */
package com.mycallstation.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.common.dao.RegistrationInvitationDAO;
import com.mycallstation.common.model.RegistrationInvitation;

/**
 * @author wgao
 * 
 */
@Repository("registrationInvitationDAO")
public class RegistrationInvitationDAOImpl extends
		AbstractDAO<RegistrationInvitation, Integer> implements
		RegistrationInvitationDAO {
}

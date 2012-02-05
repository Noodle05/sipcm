/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.RegistrationInvitationDAO;
import com.mycallstation.dataaccess.model.RegistrationInvitation;

/**
 * @author Wei Gao
 * 
 */
@Repository("registrationInvitationDAO")
public class RegistrationInvitationDAOImpl extends
		AbstractDAO<RegistrationInvitation, Integer> implements
		RegistrationInvitationDAO {
}

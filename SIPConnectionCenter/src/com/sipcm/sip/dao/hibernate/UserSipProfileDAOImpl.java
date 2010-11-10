/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.UserSipProfileDAO;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Component("userSipProfileDAO")
public class UserSipProfileDAOImpl extends AbstractDAO<UserSipProfile, Long>
		implements UserSipProfileDAO {
}

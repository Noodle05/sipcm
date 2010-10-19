/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.UserVoipAccountDAO;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
@Component("userVoipAccountDAO")
public class UserVoipAccountDAOImpl extends AbstractDAO<UserVoipAccount, Long>
		implements UserVoipAccountDAO {
}

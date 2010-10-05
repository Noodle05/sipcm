/**
 * 
 */
package com.sipcm.account.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.account.dao.UserAccountDAO;
import com.sipcm.account.model.UserAccount;
import com.sipcm.base.dao.hibernate.AbstractDAO;

/**
 * @author wgao
 * 
 */
@Component("UserAccountDAO")
public class UserAccountDAOImpl extends AbstractDAO<UserAccount, Long>
		implements UserAccountDAO {
}

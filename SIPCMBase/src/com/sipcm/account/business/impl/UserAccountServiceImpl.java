/**
 * 
 */
package com.sipcm.account.business.impl;

import javax.annotation.Resource;

import com.sipcm.account.business.UserAccountService;
import com.sipcm.account.model.UserAccount;
import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;

/**
 * @author wgao
 * 
 */
public class UserAccountServiceImpl extends AbstractService<UserAccount, Long>
		implements UserAccountService {

	@Override
	@Resource(name = "UserAccountDAO")
	public void setDAO(DAO<UserAccount, Long> dao) {
		this.dao = dao;
	}
}

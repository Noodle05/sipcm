/**
 * 
 */
package com.sipcm.sip.dao;

import com.sipcm.base.dao.Callback;
import com.sipcm.base.dao.DAO;
import com.sipcm.sip.model.UserSipBinding;

/**
 * @author wgao
 * 
 */
public interface UserSipBindingDAO extends DAO<UserSipBinding, Long> {
	public void goThoughAllEntity(Callback<UserSipBinding, Long> callback);
}

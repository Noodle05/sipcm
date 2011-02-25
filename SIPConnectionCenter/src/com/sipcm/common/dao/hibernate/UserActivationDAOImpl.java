/**
 * 
 */
package com.sipcm.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.common.dao.UserActivationDAO;
import com.sipcm.common.model.UserActivation;

/**
 * @author wgao
 * 
 */
@Repository("userActivationDAO")
public class UserActivationDAOImpl extends AbstractDAO<UserActivation, Long>
		implements UserActivationDAO {
}

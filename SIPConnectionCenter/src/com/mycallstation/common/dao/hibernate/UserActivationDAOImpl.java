/**
 * 
 */
package com.mycallstation.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.common.dao.UserActivationDAO;
import com.mycallstation.common.model.UserActivation;

/**
 * @author wgao
 * 
 */
@Repository("userActivationDAO")
public class UserActivationDAOImpl extends AbstractDAO<UserActivation, Long>
		implements UserActivationDAO {
}

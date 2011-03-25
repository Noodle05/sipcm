/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.UserActivationDAO;
import com.mycallstation.dataaccess.model.UserActivation;

/**
 * @author wgao
 * 
 */
@Repository("userActivationDAO")
public class UserActivationDAOImpl extends AbstractDAO<UserActivation, Long>
		implements UserActivationDAO {
}

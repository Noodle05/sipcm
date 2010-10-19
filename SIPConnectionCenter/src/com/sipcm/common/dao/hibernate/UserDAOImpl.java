/**
 * 
 */
package com.sipcm.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.common.dao.UserDAO;
import com.sipcm.common.model.User;

/**
 * @author Jack
 * 
 */
@Repository("userDAO")
public class UserDAOImpl extends AbstractDAO<User, Long> implements UserDAO {
}

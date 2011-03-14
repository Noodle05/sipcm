/**
 * 
 */
package com.mycallstation.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.common.dao.UserDAO;
import com.mycallstation.common.model.User;

/**
 * @author Jack
 * 
 */
@Repository("userDAO")
public class UserDAOImpl extends AbstractDAO<User, Long> implements UserDAO {
}

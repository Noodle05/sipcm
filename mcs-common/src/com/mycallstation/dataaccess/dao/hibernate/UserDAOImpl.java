/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.UserDAO;
import com.mycallstation.dataaccess.model.User;

/**
 * @author Wei Gao
 * 
 */
@Repository("userDAO")
public class UserDAOImpl extends AbstractDAO<User, Long> implements UserDAO {
}

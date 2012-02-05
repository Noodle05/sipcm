/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.RoleDAO;
import com.mycallstation.dataaccess.model.Role;

/**
 * @author Wei Gao
 * 
 */
@Repository("roleDao")
public class RoleDAOImpl extends AbstractDAO<Role, Integer> implements RoleDAO {
}

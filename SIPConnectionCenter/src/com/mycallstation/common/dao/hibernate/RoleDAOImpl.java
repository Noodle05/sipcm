/**
 * 
 */
package com.mycallstation.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.common.dao.RoleDAO;
import com.mycallstation.common.model.Role;

/**
 * @author wgao
 * 
 */
@Repository("roleDao")
public class RoleDAOImpl extends AbstractDAO<Role, Integer> implements RoleDAO {
}

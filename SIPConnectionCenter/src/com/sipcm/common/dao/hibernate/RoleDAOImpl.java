/**
 * 
 */
package com.sipcm.common.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.common.dao.RoleDAO;
import com.sipcm.common.model.Role;

/**
 * @author wgao
 * 
 */
@Component("roleDao")
public class RoleDAOImpl extends AbstractDAO<Role, Integer> implements RoleDAO {
}

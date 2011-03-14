/**
 * 
 */
package com.mycallstation.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.common.dao.ConfigValueDAO;
import com.mycallstation.common.model.ConfigValue;

/**
 * @author Jack
 * 
 */
@Repository("configDAO")
public class ConfigValueDAOImpl extends AbstractDAO<ConfigValue, Integer>
		implements ConfigValueDAO {
}

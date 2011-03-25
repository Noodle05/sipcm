/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.ConfigValueDAO;
import com.mycallstation.dataaccess.model.ConfigValue;

/**
 * @author Jack
 * 
 */
@Repository("configDAO")
public class ConfigValueDAOImpl extends AbstractDAO<ConfigValue, Integer>
		implements ConfigValueDAO {
}

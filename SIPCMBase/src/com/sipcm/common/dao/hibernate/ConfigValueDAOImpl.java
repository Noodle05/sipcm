/**
 * 
 */
package com.sipcm.common.dao.hibernate;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.common.dao.ConfigValueDAO;
import com.sipcm.common.model.ConfigValue;
import org.springframework.stereotype.Repository;

/**
 * @author Jack
 * 
 */
@Repository("configDAO")
public class ConfigValueDAOImpl extends
		AbstractDAO<ConfigValue, Integer> implements ConfigValueDAO {
}

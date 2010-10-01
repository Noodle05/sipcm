/**
 * 
 */
package com.sipcm.common.dao.hibernate;

import com.sipcm.base.dao.hibernate.AbstractDAOHibernate;
import com.sipcm.common.dao.ConfigValueDAO;
import com.sipcm.common.model.ConfigValue;
import org.springframework.stereotype.Repository;

/**
 * @author Jack
 * 
 */
@Repository("configDAO")
public class ConfigValueDAOHibernate extends
		AbstractDAOHibernate<ConfigValue, Integer> implements ConfigValueDAO {
}

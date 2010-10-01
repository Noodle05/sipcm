/**
 * 
 */
package com.sipcm.common.business.impl;

import java.util.List;

import javax.annotation.Resource;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.base.filter.Filter;
import com.sipcm.common.business.ConfigValueService;
import com.sipcm.common.model.ConfigValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jack
 * 
 */
@Service("configService")
@Transactional(readOnly = true)
public class ConfigValueServiceImpl extends
		AbstractService<ConfigValue, Integer> implements ConfigValueService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm
	 * .base.dao.DAO)
	 */
	@Override
	@Resource(name = "configDAO")
	public void setDAO(DAO<ConfigValue, Integer> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.common.business.ConfigValueService#getValueByKey(java.lang
	 * .String)
	 */
	@Override
	public ConfigValue getValueByKey(String key) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting value for \"{}\"", key);
		}
		Filter filter = filterFactory.createSimpleFilter("key", key);
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.common.business.ConfigValueService#removeAll()
	 */
	@Override
	@Transactional(readOnly = false)
	public void removeAll() {
		if (logger.isTraceEnabled()) {
			logger.trace("Removing all values");
		}
		List<ConfigValue> vs = dao.getEntities();
		dao.removeEntities(vs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.common.business.ConfigValueService#removeValueByKey(java
	 * .lang.String)
	 */
	@Override
	@Transactional(readOnly = false)
	public ConfigValue removeValueByKey(String key) {
		if (logger.isTraceEnabled()) {
			logger.trace("Removing value for key \"{}\"", key);
		}
		ConfigValue v = getValueByKey(key);
		if (v != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Found value for key \"{}\", removing it", key);
			}
			v = dao.removeEntity(v);
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Cannot find value for key \"{}\"", key);
			}
		}
		return v;
	}
}

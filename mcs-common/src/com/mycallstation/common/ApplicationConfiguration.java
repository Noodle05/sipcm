/**
 * 
 */
package com.mycallstation.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.PropertyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.business.ConfigValueService;
import com.mycallstation.dataaccess.model.ConfigValue;

/**
 * @author Jack
 * 
 */
@Component("abstractApplicationConfiguration")
public class ApplicationConfiguration extends AbstractConfiguration {
	private static final Logger logger = LoggerFactory
			.getLogger(ApplicationConfiguration.class);

	@Resource(name = "configService")
	private ConfigValueService configValueService;

	private final Lock configReadLock;
	private final Lock configWriteLock;

	public ApplicationConfiguration() {
		super();
		ReadWriteLock rwl = new ReentrantReadWriteLock();
		configReadLock = rwl.readLock();
		configWriteLock = rwl.writeLock();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.configuration.AbstractConfiguration#addPropertyDirect
	 * (java.lang.String, java.lang.Object)
	 */
	@Override
	protected void addPropertyDirect(String key, Object value) {
		if (logger.isTraceEnabled()) {
			logger.trace("Adding value \"{}\" for key \"{}\" directly",
					String.valueOf(value), key);
		}
		configWriteLock.lock();
		try {
			ConfigValue v = configValueService.createNewEntity();
			v.setKey(key);
			v.setValue(String.valueOf(value));
			configValueService.saveEntity(v);
		} catch (Exception e) {
			fireError(EVENT_ADD_PROPERTY, key, value, e);
		} finally {
			configWriteLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.configuration.AbstractConfiguration#addProperty(java
	 * .lang.String, java.lang.Object)
	 */
	@Override
	public void addProperty(String key, Object value) {
		if (logger.isTraceEnabled()) {
			logger.trace("Adding value \"{}\" for key \"{}\"",
					String.valueOf(value), key);
		}
		boolean parsingFlag = isDelimiterParsingDisabled();
		try {
			if (value instanceof String) {
				// temporarily disable delimiter parsing
				setDelimiterParsingDisabled(true);
			}
			super.addProperty(key, value);
		} finally {
			setDelimiterParsingDisabled(parsingFlag);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.configuration.AbstractConfiguration#clearProperty(
	 * java.lang.String)
	 */
	@Override
	public void clearProperty(String key) {
		if (logger.isTraceEnabled()) {
			logger.trace("Clearing value for key \"{}\"", key);
		}
		configWriteLock.lock();
		try {
			configValueService.removeValueByKey(key);
		} catch (Exception e) {
			fireError(EVENT_CLEAR_PROPERTY, key, null, e);
		} finally {
			configWriteLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.configuration.AbstractConfiguration#clear()
	 */
	@Override
	public void clear() {
		if (logger.isTraceEnabled()) {
			logger.trace("Clearing all values");
		}
		configWriteLock.lock();
		try {
			configValueService.removeAll();
		} catch (Exception e) {
			fireError(EVENT_CLEAR, null, null, e);
		} finally {
			configWriteLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.configuration.Configuration#containsKey(java.lang.
	 * String)
	 */
	@Override
	public boolean containsKey(String key) {
		if (logger.isTraceEnabled()) {
			logger.trace("Checking if key \"{}\" exists", key);
		}
		configReadLock.lock();
		try {
			ConfigValue value = configValueService.getValueByKey(key);
			if (value != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Key \"{}\" exists", key);
				}
				return true;
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Key \"{}\" doesn't exist", key);
				}
				return false;
			}
		} finally {
			configReadLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.configuration.Configuration#getKeys()
	 */
	@Override
	public Iterator<String> getKeys() {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting all keys as iterator");
		}
		configReadLock.lock();
		try {
			List<ConfigValue> values = configValueService.getEntities();
			Collection<String> keys = new ArrayList<String>(values.size());
			for (ConfigValue v : values) {
				keys.add(v.getKey());
			}
			return keys.iterator();
		} finally {
			configReadLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.configuration.Configuration#getProperty(java.lang.
	 * String)
	 */
	@Override
	public Object getProperty(String key) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting property for key \"{}\"", key);
		}
		Object result = null;
		configReadLock.lock();
		try {
			ConfigValue v = configValueService.getValueByKey(key);
			if (v != null) {
				List<Object> results = new ArrayList<Object>();
				String value = v.getValue();
				if (isDelimiterParsingDisabled()) {
					results.add(value);
				} else {
					// Split value if it containts the list delimiter
					CollectionUtils.addAll(results, PropertyConverter
							.toIterator(value, getListDelimiter()));
				}
				if (!results.isEmpty()) {
					result = (results.size() > 1) ? results : results.get(0);
				}
			}
		} catch (Exception e) {
			fireError(EVENT_READ_PROPERTY, key, null, e);
		} finally {
			configReadLock.unlock();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.configuration.Configuration#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		if (logger.isTraceEnabled()) {
			logger.trace("Checking if configuration is empty");
		}
		configReadLock.lock();
		try {
			return (configValueService.getRowCount() > 0);
		} finally {
			configReadLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.configuration.AbstractConfiguration#setProperty(java
	 * .lang.String, java.lang.Object)
	 */
	@Override
	public void setProperty(String key, Object value) {
		if (logger.isTraceEnabled()) {
			logger.trace("Setting key \"{}\" to value \"{}\"", key,
					String.valueOf(value));
		}
		fireEvent(EVENT_SET_PROPERTY, key, value, true);
		setDetailEvents(false);
		configWriteLock.lock();
		try {
			ConfigValue v = configValueService.getValueByKey(key);
			if (v == null) {
				v = configValueService.createNewEntity();
				v.setKey(key);
			}
			v.setValue(String.valueOf(value));
			configValueService.saveEntity(v);
		} finally {
			configWriteLock.unlock();
			setDetailEvents(true);
		}
		fireEvent(EVENT_SET_PROPERTY, key, value, false);
	}
}

/**
 * 
 */
package com.mycallstation.base.filter;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.base.filter.impl.FilterFactoryImpl;

/**
 * @author Jack
 * 
 */
public abstract class FilterFactory {
	private static final Logger logger = LoggerFactory
			.getLogger(FilterFactory.class);

	public static final String defaultFilterFactoryClass = FilterFactoryImpl.class
			.getCanonicalName();

	public static FilterFactory getDefaultFilterFactory() {
		FilterFactory filterFactory = null;
		try {
			Class<?> clazz = Class.forName(defaultFilterFactoryClass);
			if (clazz.isAssignableFrom(FilterFactory.class)) {
				filterFactory = (FilterFactory) clazz.newInstance();
			} else {
				throw new ClassNotFoundException(defaultFilterFactoryClass
						+ " is not subclass of FilterFactory.");
			}
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Default filter factory class doesn't exists.", e);
			}
		} catch (IllegalAccessException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Cannot access default filter factory class.", e);
			}
		} catch (InstantiationException e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"Cannot instantiate default filter factory class.", e);
			}
		}
		return filterFactory;
	}

	/**
	 * Create simple filter by name of property and object value.
	 * 
	 * @param name
	 * @param val
	 * @return Filter object.
	 */
	public abstract Filter createSimpleFilter(String name, Serializable val);

	/**
	 * Create simple filter by logic name of bean object, name of property,
	 * value and operator.
	 * 
	 * @param name
	 * @param val
	 * @param op
	 * @return Filter object.
	 */
	public abstract Filter createSimpleFilter(String name, Serializable val,
			Filter.Operator op);

	/**
	 * Create simple filter by string of condition.
	 * 
	 * @param strCondition
	 * @return Filter object.
	 */
	public abstract Filter createSimpleFilter(String strCondition);

	/**
	 * Create between filter by name of property and two values.
	 * 
	 * @param name
	 * @param val1
	 * @param val2
	 * @return Filter object.
	 */
	public abstract Filter createBetweenFilter(String name, Serializable val1,
			Serializable val2);

	/**
	 * Create between filter by name of property and two values.
	 * 
	 * @param name
	 * @param val1
	 * @param val2
	 * @return Filter object.
	 */
	public abstract Filter createNotBetweenFilter(String name,
			Serializable val1, Serializable val2);

	/**
	 * Create in filter by name of property and list of values.
	 * 
	 * @param name
	 * @param values
	 * @return Filter object.
	 */
	public abstract <T extends Serializable> Filter createInFilter(String name,
			List<T> values);

	/**
	 * Create in filter by name of property and list of values.
	 * 
	 * @param name
	 * @param values
	 * @return Filter object.
	 */
	public abstract <T extends Serializable> Filter createInFilter(String name,
			T... values);

	/**
	 * Create not in filter by name of property and list of values.
	 * 
	 * @param name
	 * @param values
	 * @return Filter object.
	 */
	public abstract Filter createNotInFilter(String name,
			List<? extends Serializable> values);

	/**
	 * Create not in filter by name of property and list of values.
	 * 
	 * @param name
	 * @param values
	 * @return Filter object.
	 */
	public abstract <T extends Serializable> Filter createNotInFilter(
			String name, T... values);

	/**
	 * Create is null filter by name of property.
	 * 
	 * @param name
	 * @return Filter object.
	 */
	public abstract Filter createIsNullFilter(String name);

	/**
	 * Create not null filter by name of property.
	 * 
	 * @param name
	 * @return Filter object.
	 */
	public abstract Filter createIsNotNullFilter(String name);

	/**
	 * Create a instance of sort object. with direction: asc
	 * 
	 * @param name
	 * @return sort
	 */
	public abstract Sort createSort(String varName);

	/**
	 * Create a instance of sort object.
	 * 
	 * @param name
	 * @param direction
	 * @return sort
	 */
	public abstract Sort createSort(String varName, Sort.Direction direction);

	/**
	 * Create a page object.
	 * 
	 * @return
	 */
	public abstract Page createPage();
}

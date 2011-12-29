/**
 * 
 */
package com.mycallstation.base.filter;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jack
 * 
 */
public interface FilterFactory {
	/**
	 * Create simple filter by name of property and object value.
	 * 
	 * @param name
	 * @param val
	 * @return Filter object.
	 */
	public Filter createSimpleFilter(String name, Serializable val);

	/**
	 * Create simple filter by logic name of bean object, name of property,
	 * value and operator.
	 * 
	 * @param name
	 * @param val
	 * @param op
	 * @return Filter object.
	 */
	public Filter createSimpleFilter(String name, Serializable val, Operator op);

	/**
	 * Create simple filter by string of condition.
	 * 
	 * @param strCondition
	 * @return Filter object.
	 */
	public Filter createSimpleFilter(String strCondition);

	/**
	 * Create between filter by name of property and two values.
	 * 
	 * @param name
	 * @param val1
	 * @param val2
	 * @return Filter object.
	 */
	public Filter createBetweenFilter(String name, Serializable val1,
			Serializable val2);

	/**
	 * Create between filter by name of property and two values.
	 * 
	 * @param name
	 * @param val1
	 * @param val2
	 * @return Filter object.
	 */
	public Filter createNotBetweenFilter(String name, Serializable val1,
			Serializable val2);

	/**
	 * Create in filter by name of property and list of values.
	 * 
	 * @param name
	 * @param values
	 * @return Filter object.
	 */
	public <T extends Serializable> Filter createInFilter(String name,
			List<T> values);

	/**
	 * Create in filter by name of property and list of values.
	 * 
	 * @param name
	 * @param values
	 * @return Filter object.
	 */
	public <T extends Serializable> Filter createInFilter(String name,
			T... values);

	/**
	 * Create not in filter by name of property and list of values.
	 * 
	 * @param name
	 * @param values
	 * @return Filter object.
	 */
	public Filter createNotInFilter(String name,
			List<? extends Serializable> values);

	/**
	 * Create not in filter by name of property and list of values.
	 * 
	 * @param name
	 * @param values
	 * @return Filter object.
	 */
	public <T extends Serializable> Filter createNotInFilter(String name,
			T... values);

	/**
	 * Create is null filter by name of property.
	 * 
	 * @param name
	 * @return Filter object.
	 */
	public Filter createIsNullFilter(String name);

	/**
	 * Create not null filter by name of property.
	 * 
	 * @param name
	 * @return Filter object.
	 */
	public Filter createIsNotNullFilter(String name);

	/**
	 * Create is empty filter by name of property. This can be used on
	 * one-to-many or many-to-many collection properties.
	 * 
	 * @param name
	 * @return Filter object.
	 */
	public Filter createIsEmptyFilter(String name);

	/**
	 * Create not empty filter by name of property. This can be used on
	 * one-to-many or many-to-many collection properties.
	 * 
	 * @param name
	 * @return Filter object.
	 */
	public Filter createIsNotEmptyFilter(String name);

	/**
	 * Create member of filter that can be used on one-to-many or many-to-many
	 * properties. For value is member of a collection property.
	 * 
	 * @param name
	 * @return Filter object.
	 */
	public <T extends Serializable> Filter createMemberOfFilter(String name,
			T value);

	/**
	 * Create not member of filter that can be used on one-to-many or
	 * many-to-many properties. For value is not member of a collection
	 * property.
	 * 
	 * @param name
	 * @return Filter object.
	 */
	public <T extends Serializable> Filter createNotMemberOfFilter(String name,
			T value);

	/**
	 * Create a instance of sort object. with direction: asc
	 * 
	 * @param name
	 * @return sort
	 */
	public Sort createSort(String varName);

	/**
	 * Create a instance of sort object.
	 * 
	 * @param name
	 * @param direction
	 * @return sort
	 */
	public Sort createSort(String varName, Direction direction);

	/**
	 * Create a page object.
	 * 
	 * @return
	 */
	public Page createPage();
}

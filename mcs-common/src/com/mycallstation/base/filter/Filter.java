/**
 * 
 */
package com.mycallstation.base.filter;

import java.io.Serializable;
import java.util.List;

/**
 * @author Wei Gao
 * 
 */
public interface Filter {
	/**
	 * Used for generated sql statement. As default alias name of the table.
	 */
	public static final String DEFAULT_ALIAS = "_default_";

	/**
	 * Append this filter with another filter with "AND" operator. Return new
	 * filter. If the filter is not append-able, InvalidFilterException thrown.
	 * 
	 * @param filter
	 * @return the filter
	 * @throws InvalidFilterException
	 */
	public Filter appendAnd(Filter filter) throws InvalidFilterException;

	/**
	 * Append this filter with another filter with "OR" operator. Return new
	 * filter. If filter is not append-able, InvalidFilterException thrown.
	 * 
	 * @param filter
	 * @return the filter
	 * @throws InvalidFilterException
	 */
	public Filter appendOr(Filter filter) throws InvalidFilterException;

	/**
	 * Get string of the filter. String should HQL statement. values hold by
	 * question mark.
	 * 
	 * @return the string
	 * @throws InvalidFilterException
	 */
	public String getString() throws InvalidFilterException;

	/**
	 * Get values of the filter. values ordered as they appeared in string
	 * order.
	 * 
	 * @return the values
	 * @throws InvalidFilterException
	 */
	public List<Serializable> getValues() throws InvalidFilterException;
}

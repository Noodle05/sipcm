/**
 * 
 */
package com.sipcm.base.filter;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jack
 * 
 */
public interface Filter {
	static enum Operator {
		NONE(null), LIKE("like"), NOT_LIKE("not like"), ILIKE("like"), NOT_ILIKE(
				"not like"), EQ("="), NOT_EQ("<>"), IEQ("="), NOT_IEQ("<>"), GREATER_THAN(
				">"), LESS_THAN("<"), GREATER_EQ(">="), LESS_EQ("<="), IN("in"), NOT_IN(
				"not in"), IIN("in"), NOT_IIN("not in"), AND("and"), OR("or"), BETWEEN(
				"between"), NOT_BETWEEN("not between"), EXISTS("if exists"), NOT_EXISTS(
				"if not exists"), NULL("is null"), NOT_NULL("is not null");

		private String operator;

		private Operator(String operator) {
			this.operator = operator;
		}

		public boolean isCaseInsensitvieOperator() {
			switch (this) {
			case IEQ:
			case NOT_IEQ:
			case ILIKE:
			case NOT_ILIKE:
			case IIN:
			case NOT_IIN:
				return true;
			default:
				return false;
			}
		}

		public boolean isSimpleOperator() {
			switch (this) {
			case EQ:
			case NOT_EQ:
			case IEQ:
			case NOT_IEQ:
			case LIKE:
			case NOT_LIKE:
			case ILIKE:
			case NOT_ILIKE:
			case GREATER_EQ:
			case GREATER_THAN:
			case LESS_EQ:
			case LESS_THAN:
				return true;
			default:
				return false;
			}
		}

		public String getString() {
			return operator;
		}
	};

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

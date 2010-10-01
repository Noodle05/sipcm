/**
 * 
 */
package com.sipcm.base.filter.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.sipcm.base.filter.Filter;

/**
 * @author Jack
 * 
 */
class IsNullFilter extends BaseFilter implements Serializable {
	private static final long serialVersionUID = -7782355728640776213L;

	private String property;

	private Operator operator;

	IsNullFilter(String name, boolean notFlag) {
		property = name;
		operator = notFlag ? Operator.NOT_NULL : Operator.NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.filter.Filter#getString()
	 */
	@Override
	public String getString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Filter.DEFAULT_ALIAS).append(".").append(property)
				.append(" ").append(operator.getString());
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.filter.Filter#getValues()
	 */
	@Override
	public List<Serializable> getValues() {
		return Collections.emptyList();
	}
}

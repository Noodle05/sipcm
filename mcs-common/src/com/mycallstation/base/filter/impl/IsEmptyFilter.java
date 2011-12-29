/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.Operator;

/**
 * @author Jack
 * 
 */
class IsEmptyFilter extends BaseFilter implements Serializable {
	private static final long serialVersionUID = -7782355728640776213L;

	private final String property;

	private final Operator operator;

	IsEmptyFilter(String name, boolean notFlag) {
		property = name;
		operator = notFlag ? Operator.NOT_NULL : Operator.NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getString()
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
	 * @see com.mycallstation.base.filter.Filter#getValues()
	 */
	@Override
	public List<Serializable> getValues() {
		return Collections.emptyList();
	}
}

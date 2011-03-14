/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.mycallstation.base.filter.Filter;

/**
 * @author Jack
 * 
 */
class ExistFilter extends BaseFilter implements Serializable {
	private static final long serialVersionUID = -8938735745979348763L;

	protected StringFilter selectQuery;

	protected Operator operator;

	ExistFilter(StringFilter selectQuery, boolean notFlag) {
		this.selectQuery = selectQuery;
		operator = notFlag ? Operator.NOT_EXISTS : Operator.EXISTS;
	}

	public Filter getLeftHand() {
		return null;
	}

	public Filter getRightHand() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getString()
	 */
	@Override
	public String getString() {
		StringBuilder sb = new StringBuilder();
		sb.append(operator.getString()).append(" ")
				.append(selectQuery.getString());
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

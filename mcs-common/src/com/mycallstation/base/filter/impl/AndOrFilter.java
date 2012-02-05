/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.InvalidFilterException;
import com.mycallstation.base.filter.Operator;

/**
 * @author Wei Gao
 * 
 */
class AndOrFilter extends BaseFilter implements Serializable {
	private static final long serialVersionUID = -8473349893716745816L;

	private final Filter leftHand;

	private final Filter rightHand;

	private final Operator operator;

	AndOrFilter(Filter leftHand, Filter rightHand, boolean and) {
		this.leftHand = leftHand;
		this.rightHand = rightHand;
		if (and) {
			operator = Operator.AND;
		} else {
			operator = Operator.OR;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getString()
	 */
	@Override
	public String getString() throws InvalidFilterException {
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(leftHand.getString()).append(") ")
				.append(operator.getString()).append(" (")
				.append(rightHand.getString()).append(")");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getValues()
	 */
	@Override
	public List<Serializable> getValues() throws InvalidFilterException {
		List<Serializable> ret = new ArrayList<Serializable>();
		ret.addAll(leftHand.getValues());
		ret.addAll(rightHand.getValues());
		return ret;
	}
}

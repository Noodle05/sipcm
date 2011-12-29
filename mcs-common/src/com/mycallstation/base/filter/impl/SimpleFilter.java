/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.Operator;

/**
 * @author Jack
 * 
 */
class SimpleFilter extends BaseFilter implements Serializable {
	private static final long serialVersionUID = 685227069924405650L;

	private final String leftHand;

	private final Serializable rightHand;

	private final Operator operator;

	SimpleFilter(Operator operator, String leftHand, Serializable rightHand) {
		this.operator = operator;
		this.leftHand = leftHand;
		this.rightHand = rightHand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getString()
	 */
	@Override
	public String getString() {
		StringBuilder sb = new StringBuilder();
		if (operator.isCaseInsensitvieOperator()) {
			sb.append("upper(");
		}
		sb.append(Filter.DEFAULT_ALIAS).append(".").append(leftHand);
		if (operator.isCaseInsensitvieOperator()) {
			sb.append(")");
		}
		sb.append(" ").append(operator.getString()).append(" ?");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getValues()
	 */
	@Override
	public List<Serializable> getValues() {
		List<Serializable> ret = new ArrayList<Serializable>(1);
		if (operator.isCaseInsensitvieOperator()
				&& (rightHand instanceof String)) {
			ret.add(((String) rightHand).toUpperCase());
		} else {
			ret.add(rightHand);
		}
		return ret;
	}
}

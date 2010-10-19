/**
 * 
 */
package com.sipcm.base.filter.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sipcm.base.filter.Filter;

/**
 * @author Jack
 * 
 */
class SimpleFilter extends BaseFilter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 685227069924405650L;

	protected String leftHand;

	protected Serializable rightHand;

	protected Filter.Operator operator;

	SimpleFilter(Operator operator, String leftHand, Serializable rightHand) {
		this.operator = operator;
		this.leftHand = leftHand;
		this.rightHand = rightHand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.filter.Filter#getString()
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
	 * @see com.sipcm.base.filter.Filter#getValues()
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

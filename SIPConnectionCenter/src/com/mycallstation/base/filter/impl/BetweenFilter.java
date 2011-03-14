/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mycallstation.base.filter.Filter;

/**
 * @author Jack
 * 
 */
class BetweenFilter extends BaseFilter implements Serializable {
	private static final long serialVersionUID = 9049839710701185793L;

	private String leftHand;

	private Serializable rightHand1;

	private Serializable rightHand2;

	private Operator operator;

	BetweenFilter(String left, Serializable right1, Serializable right2,
			boolean notFlag) {
		this.leftHand = left;
		this.rightHand1 = right1;
		this.rightHand2 = right2;
		operator = notFlag ? Operator.NOT_BETWEEN : Operator.BETWEEN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getString()
	 */
	@Override
	public String getString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Filter.DEFAULT_ALIAS).append(".").append(leftHand)
				.append(" ").append(operator.getString()).append(" ? and ?");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getValues()
	 */
	@Override
	public List<Serializable> getValues() {
		List<Serializable> ret = new ArrayList<Serializable>(2);
		ret.add(rightHand1);
		ret.add(rightHand2);
		return ret;
	}
}

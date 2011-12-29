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
class MemberOfFilter extends BaseFilter implements Serializable {
	private static final long serialVersionUID = 685227069924405650L;

	private final String leftHand;

	private final Serializable rightHand;

	private final Operator operator;

	MemberOfFilter(String name, Serializable value, boolean notFlag) {
		this.leftHand = name;
		this.rightHand = value;
		this.operator = notFlag ? Operator.NOT_MEMBER_OF : Operator.MEMBER_OF;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getString()
	 */
	@Override
	public String getString() {
		StringBuilder sb = new StringBuilder();
		sb.append("? ").append(operator.getString()).append(" ")
				.append(Filter.DEFAULT_ALIAS).append(".").append(leftHand);
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
		ret.add(rightHand);
		return ret;
	}
}

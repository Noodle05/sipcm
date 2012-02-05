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
 * @author Wei Gao
 * 
 */
class InListFilter extends BaseInFilter implements Serializable {
	private static final long serialVersionUID = 910431459453687700L;

	private final List<? extends Serializable> rightHand;

	<T extends Serializable> InListFilter(String left, List<T> right,
			boolean notFlag) {
		super(left, notFlag);
		this.rightHand = right;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getString()
	 */
	@Override
	public String getString() {
		StringBuilder sb = new StringBuilder();
		if (rightHand == null || rightHand.isEmpty()) {
			sb.append(Operator.NOT_IN.equals(operator) ? "1 = 1" : "0 = 1");
		} else {
			sb.append(Filter.DEFAULT_ALIAS).append(".").append(leftHand)
					.append(" ").append(operator.getString()).append(" (");
			boolean first = true;
			for (@SuppressWarnings("unused")
			Serializable rh : rightHand) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append("?");
			}
			sb.append(")");
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Filter#getValues()
	 */
	@Override
	public List<Serializable> getValues() {
		List<Serializable> ret = new ArrayList<Serializable>(rightHand);
		return ret;
	}
}

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
class InListFilter extends BaseInFilter implements Serializable {
	private static final long serialVersionUID = 910431459453687700L;

	private List<? extends Serializable> rightHand;

	InListFilter(String left, List<? extends Serializable> right,
			boolean notFlag) {
		super(left, notFlag);
		this.rightHand = right;
	}

	<T extends Serializable> InListFilter(String left, boolean notFlag,
			T... right) {
		super(left, notFlag);
		if (right != null) {
			List<T> rh = new ArrayList<T>(right.length);
			for (T r : right) {
				rh.add(r);
			}
			this.rightHand = rh;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.filter.Filter#getString()
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
	 * @see com.sipcm.base.filter.Filter#getValues()
	 */
	@Override
	public List<Serializable> getValues() {
		List<Serializable> ret = new ArrayList<Serializable>(rightHand);
		return ret;
	}
}

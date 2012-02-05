/**
 * 
 */
package com.mycallstation.base.filter.impl;

import com.mycallstation.base.filter.Filter;

/**
 * @author Wei Gao
 * 
 */
abstract class BaseFilter implements Filter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.Filter#appendAnd(com.mycallstation.base
	 * .filter.Filter)
	 */
	@Override
	public Filter appendAnd(Filter filter) {
		Filter ret = this;
		if (filter != null) {
			ret = new AndOrFilter(this, filter, true);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.Filter#appendOr(com.mycallstation.base.
	 * filter.Filter)
	 */
	@Override
	public Filter appendOr(Filter filter) {
		Filter ret = this;
		if (filter != null) {
			ret = new AndOrFilter(this, filter, false);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getString();
	}
}

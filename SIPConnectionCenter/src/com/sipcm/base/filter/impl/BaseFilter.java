/**
 * 
 */
package com.sipcm.base.filter.impl;

import com.sipcm.base.filter.Filter;

/**
 * @author Jack
 * 
 */
abstract class BaseFilter implements Filter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.filter.Filter#appendAnd(com.sipcm.base.filter.
	 * Filter)
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
	 * com.sipcm.base.filter.Filter#appendOr(com.sipcm.base.filter.Filter
	 * )
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

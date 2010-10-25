/**
 * 
 */
package com.sipcm.base.filter.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Jack
 * 
 */
class StringFilter extends BaseFilter implements Serializable {
	private static final long serialVersionUID = 8168643045904058467L;

	private String strCondition;

	StringFilter(String condition) {
		this.strCondition = condition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.filter.Filter#getString()
	 */
	@Override
	public String getString() {
		return strCondition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.filter.Filter#getValues()
	 */
	@Override
	public List<Serializable> getValues() {
		return Collections.emptyList();
	}
}
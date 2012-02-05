/**
 * 
 */
package com.mycallstation.base.filter.impl;

import com.mycallstation.base.filter.Operator;

/**
 * @author Wei Gao
 * 
 */
abstract class BaseInFilter extends BaseFilter {
	protected final String leftHand;

	protected final Operator operator;

	BaseInFilter(String leftHand, boolean notFlag) {
		this.leftHand = leftHand;
		operator = notFlag ? Operator.NOT_IN : Operator.IN;
	}
}

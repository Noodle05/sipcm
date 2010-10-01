/**
 * 
 */
package com.sipcm.base.filter.impl;

/**
 * @author Jack
 * 
 */
abstract class BaseInFilter extends BaseFilter {
	protected String leftHand;

	protected Operator operator;

	BaseInFilter(String leftHand, boolean notFlag) {
		this.leftHand = leftHand;
		operator = notFlag ? Operator.NOT_IN : Operator.IN;
	}
}

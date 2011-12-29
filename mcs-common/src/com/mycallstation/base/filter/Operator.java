/**
 * 
 */
package com.mycallstation.base.filter;

/**
 * @author wgao
 * 
 */
public enum Operator {
	NONE(null), LIKE("like"), NOT_LIKE("not like"), ILIKE("like"), NOT_ILIKE(
			"not like"), EQ("="), NOT_EQ("<>"), IEQ("="), NOT_IEQ("<>"), GREATER_THAN(
			">"), LESS_THAN("<"), GREATER_EQ(">="), LESS_EQ("<="), IN("in"), NOT_IN(
			"not in"), IIN("in"), NOT_IIN("not in"), AND("and"), OR("or"), BETWEEN(
			"between"), NOT_BETWEEN("not between"), EXISTS("if exists"), NOT_EXISTS(
			"if not exists"), NULL("is null"), NOT_NULL("is not null"), EMPTY(
			"is empty"), NOT_EMPTY("is not empty"), MEMBER_OF("member of"), NOT_MEMBER_OF(
			"not member of");

	private String operator;

	private Operator(String operator) {
		this.operator = operator;
	}

	public boolean isCaseInsensitvieOperator() {
		switch (this) {
		case IEQ:
		case NOT_IEQ:
		case ILIKE:
		case NOT_ILIKE:
		case IIN:
		case NOT_IIN:
			return true;
		default:
			return false;
		}
	}

	public boolean isSimpleOperator() {
		switch (this) {
		case EQ:
		case NOT_EQ:
		case IEQ:
		case NOT_IEQ:
		case LIKE:
		case NOT_LIKE:
		case ILIKE:
		case NOT_ILIKE:
		case GREATER_EQ:
		case GREATER_THAN:
		case LESS_EQ:
		case LESS_THAN:
			return true;
		default:
			return false;
		}
	}

	public String getString() {
		return operator;
	}
}

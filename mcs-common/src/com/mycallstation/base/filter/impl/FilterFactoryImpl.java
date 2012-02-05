/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.mycallstation.base.filter.Direction;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.FilterFactory;
import com.mycallstation.base.filter.InvalidFilterException;
import com.mycallstation.base.filter.Operator;
import com.mycallstation.base.filter.Page;
import com.mycallstation.base.filter.Sort;

/**
 * @author Wei Gao
 * 
 */
@Component("filterFactory")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class FilterFactoryImpl implements FilterFactory {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createSimpleFilter(java.lang
	 * .String, java.io.Serializable)
	 */
	@Override
	public Filter createSimpleFilter(String name, Serializable val) {
		return createSimpleFilter(name, val, Operator.EQ);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createSimpleFilter(java.lang
	 * .String, java.io.Serializable,
	 * com.mycallstation.base.filter.Filter.Operator)
	 */
	@Override
	public Filter createSimpleFilter(String name, Serializable val, Operator op) {
		checkArguments(name, op);
		// Check OPERATOR. Not all operator can be create as simple filter.
		if (!op.isSimpleOperator()) {
			throw new InvalidFilterException(
					"Can not create simple filter for " + op);
		}
		Filter ret = null;
		if (val == null) {
			ret = new IsNullFilter(name, false);
		} else {
			ret = new SimpleFilter(op, name, val);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createSimpleFilter(java.lang
	 * .String)
	 */
	@Override
	public Filter createSimpleFilter(String strCondition) {
		return new StringFilter(strCondition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createBetweenFilter(java.
	 * lang.String, java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public Filter createBetweenFilter(String name, Serializable val1,
			Serializable val2) {
		checkArguments(name);
		BetweenFilter filter = new BetweenFilter(name, val1, val2, false);
		return filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createNotBetweenFilter(java
	 * .lang.String, java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public Filter createNotBetweenFilter(String name, Serializable val1,
			Serializable val2) {
		checkArguments(name);
		BetweenFilter filter = new BetweenFilter(name, val1, val2, true);
		return filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createInFilter(java.lang.
	 * String, java.util.List)
	 */
	@Override
	public <T extends Serializable> Filter createInFilter(String name,
			List<T> values) {
		checkArguments(name);
		return new InListFilter(name, values, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createInFilter(java.lang.
	 * String, T[])
	 */
	@Override
	public <T extends Serializable> Filter createInFilter(String name,
			T... values) {
		checkArguments(name);
		return new InListFilter(name, values == null ? null
				: Arrays.asList(values), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createNotInFilter(java.lang
	 * .String, java.util.List)
	 */
	@Override
	public Filter createNotInFilter(String name,
			List<? extends Serializable> values) {
		checkArguments(name);
		return new InListFilter(name, values, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createNotInFilter(java.lang
	 * .String, T[])
	 */
	@Override
	public <T extends Serializable> Filter createNotInFilter(String name,
			T... values) {
		checkArguments(name);
		return new InListFilter(name, values == null ? null
				: Arrays.asList(values), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createIsNullFilter(java.lang
	 * .String)
	 */
	@Override
	public Filter createIsNullFilter(String name) {
		checkArguments(name);
		return new IsNullFilter(name, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createIsNotNullFilter(java
	 * .lang.String)
	 */
	@Override
	public Filter createIsNotNullFilter(String name) {
		checkArguments(name);
		return new IsNullFilter(name, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createIsEmptyFilter(java.
	 * lang.String)
	 */
	@Override
	public Filter createIsEmptyFilter(String name) {
		checkArguments(name);
		return new IsEmptyFilter(name, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createIsNotEmptyFilter(java
	 * .lang.String)
	 */
	@Override
	public Filter createIsNotEmptyFilter(String name) {
		checkArguments(name);
		return new IsEmptyFilter(name, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createMemberOfFilter(java
	 * .lang.String, java.io.Serializable)
	 */
	@Override
	public <T extends Serializable> Filter createMemberOfFilter(String name,
			T value) {
		checkArguments(name);
		return new MemberOfFilter(name, value, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createNotMemberOfFilter(java
	 * .lang.String, java.io.Serializable)
	 */
	@Override
	public <T extends Serializable> Filter createNotMemberOfFilter(String name,
			T value) {
		checkArguments(name);
		return new MemberOfFilter(name, value, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createSort(java.lang.String)
	 */
	@Override
	public Sort createSort(String varName) {
		Sort ret = new SortImpl(varName);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.FilterFactory#createSort(java.lang.String,
	 * com.mycallstation.base.filter.Direction)
	 */
	@Override
	public Sort createSort(String varName, Direction direction) {
		Sort ret = new SortImpl(varName, direction);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.FilterFactory#createPage()
	 */
	@Override
	public Page createPage() {
		return new PageImpl();
	}

	private void checkArguments(String name, Operator op) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("name cannot be empty.");
		}
		if (op == null) {
			throw new IllegalArgumentException("Operator cannot be null.");
		}
	}

	private void checkArguments(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("name cannot be empty.");
		}
	}
}

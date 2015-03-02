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
	@Override
	public Filter createSimpleFilter(String name, Serializable val) {
		return createSimpleFilter(name, val, Operator.EQ);
	}

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

	@Override
	public Filter createSimpleFilter(String strCondition) {
		return new StringFilter(strCondition);
	}

	@Override
	public Filter createBetweenFilter(String name, Serializable val1,
			Serializable val2) {
		checkArguments(name);
		BetweenFilter filter = new BetweenFilter(name, val1, val2, false);
		return filter;
	}

	@Override
	public Filter createNotBetweenFilter(String name, Serializable val1,
			Serializable val2) {
		checkArguments(name);
		BetweenFilter filter = new BetweenFilter(name, val1, val2, true);
		return filter;
	}

	@Override
	public <T extends Serializable> Filter createInFilter(String name,
			List<T> values) {
		checkArguments(name);
		return new InListFilter(name, values, false);
	}

	@Override
	public <T extends Serializable> Filter createInFilter(String name,
			T... values) {
		checkArguments(name);
		return new InListFilter(name, values == null ? null
				: Arrays.asList(values), false);
	}

	@Override
	public Filter createNotInFilter(String name,
			List<? extends Serializable> values) {
		checkArguments(name);
		return new InListFilter(name, values, true);
	}

	@Override
	public <T extends Serializable> Filter createNotInFilter(String name,
			T... values) {
		checkArguments(name);
		return new InListFilter(name, values == null ? null
				: Arrays.asList(values), true);
	}

	@Override
	public Filter createIsNullFilter(String name) {
		checkArguments(name);
		return new IsNullFilter(name, false);
	}

	@Override
	public Filter createIsNotNullFilter(String name) {
		checkArguments(name);
		return new IsNullFilter(name, true);
	}

	@Override
	public Filter createIsEmptyFilter(String name) {
		checkArguments(name);
		return new IsEmptyFilter(name, false);
	}

	@Override
	public Filter createIsNotEmptyFilter(String name) {
		checkArguments(name);
		return new IsEmptyFilter(name, true);
	}

	@Override
	public <T extends Serializable> Filter createMemberOfFilter(String name,
			T value) {
		checkArguments(name);
		return new MemberOfFilter(name, value, false);
	}

	@Override
	public <T extends Serializable> Filter createNotMemberOfFilter(String name,
			T value) {
		checkArguments(name);
		return new MemberOfFilter(name, value, true);
	}

	@Override
	public Sort createSort(String varName) {
		Sort ret = new SortImpl(varName);
		return ret;
	}

	@Override
	public Sort createSort(String varName, Direction direction) {
		Sort ret = new SortImpl(varName, direction);
		return ret;
	}

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

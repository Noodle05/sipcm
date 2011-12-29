/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;

import com.mycallstation.base.filter.Direction;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.Sort;

/**
 * @author Jack
 * 
 */
class SortImpl implements Sort, Serializable, Cloneable {
	private static final long serialVersionUID = 2231061244497829862L;

	private final String propertyName;

	private final Direction direction;

	private SortImpl next;

	SortImpl(String varName) {
		this.propertyName = varName;
		this.direction = Direction.ASC;
	}

	SortImpl(String varName, Direction direction) {
		this.propertyName = varName;
		this.direction = direction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.filter.Sort#appendSort(com.mycallstation.base.
	 * filter.Sort)
	 */
	@Override
	public Sort appendSort(Sort next) {
		if (next instanceof SortImpl == false) {
			throw new IllegalArgumentException(
					"Can only append same implementation of Sort.");
		}
		if (this.next == null) {
			this.next = (SortImpl) next;
		} else {
			this.next.appendSort(next);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Sort clone() {
		Sort sort = new SortImpl(propertyName, direction);
		if (next != null) {
			sort.appendSort(next.clone());
		}
		return sort;
	}

	private StringBuilder toSortString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Filter.DEFAULT_ALIAS).append(".").append(propertyName);
		if (Direction.ASC.equals(direction)) {
			sb.append(" asc");
		} else {
			sb.append(" desc");
		}
		return sb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(toSortString());
		if (next != null) {
			sb.append(", ");
			sb.append(next.toSortString());
		}
		return sb.toString();
	}
}

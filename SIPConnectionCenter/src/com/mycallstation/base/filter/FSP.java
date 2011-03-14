/**
 * 
 */
package com.mycallstation.base.filter;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Jack
 * 
 */
public class FSP implements Serializable {
	private static final long serialVersionUID = 2101325740620337034L;

	private Filter filter;

	private Sort sort;

	private Page page;

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public Sort getSort() {
		return sort;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Page getPage() {
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("Page", page);
		tsb.append("Sort", sort);
		tsb.append("Filter", filter);
		return tsb.toString();
	}
}

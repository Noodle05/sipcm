/**
 * 
 */
package com.mycallstation.base.filter;

import java.io.Serializable;

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
		StringBuilder sb = new StringBuilder();
		sb.append("FSP[");
		boolean first = true;
		if (filter != null) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append("filter=").append(filter);
		}
		if (sort != null) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append("sort=").append(sort);
		}
		if (page != null) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append("page=").append(page);
		}
		sb.append("]");
		return sb.toString();
	}
}

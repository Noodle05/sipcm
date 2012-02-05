/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;

import com.mycallstation.base.filter.Page;

/**
 * @author Wei Gao
 * 
 */
class PageImpl implements Page, Serializable {
	private static final long serialVersionUID = 2245620142094499076L;

	private int currentPage = 1;

	private int recordsPerPage = RECORDS_PER_PAGE;

	private int totalRecords = 0;

	private boolean tooManySearchReturn = false;

	private int startRowPosition = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#getCurrentPage()
	 */
	@Override
	public int getCurrentPage() {
		return currentPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#getEndRowPosition()
	 */
	@Override
	public int getEndRowPosition() {
		int max = recordsPerPage * getCurrentPage();
		return max;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#getRecordsPerPage()
	 */
	@Override
	public int getRecordsPerPage() {
		if (recordsPerPage == 0) {
			recordsPerPage = RECORDS_PER_PAGE;
		}
		return recordsPerPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#getStartRowPosition()
	 */
	@Override
	public int getStartRowPosition() {
		if (startRowPosition < 0) {
			if (recordsPerPage >= 0) {
				return recordsPerPage * (getCurrentPage() - 1);
			} else {
				return 0;
			}
		} else {
			return startRowPosition;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#getTotalPages()
	 */
	@Override
	public int getTotalPages() {
		if (recordsPerPage < 0) {
			return 1;
		} else {
			return (totalRecords + recordsPerPage - 1) / recordsPerPage;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#getTotalRecords()
	 */
	@Override
	public int getTotalRecords() {
		return totalRecords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#isTooManySearchReturn()
	 */
	@Override
	public boolean isTooManySearchReturn() {
		return tooManySearchReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#setCurrentPage(int)
	 */
	@Override
	public void setCurrentPage(int currentPage) {
		if (currentPage > 0) {
			this.currentPage = currentPage;
		} else {
			this.currentPage = 1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#setRecordsPerPage(int)
	 */
	@Override
	public void setRecordsPerPage(int records) {
		if (records > 0 || records < 0) {
			this.recordsPerPage = records;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#setStartRowPosition(int)
	 */
	@Override
	public void setStartRowPosition(int startRowPosition) {
		if (startRowPosition >= 0) {
			this.startRowPosition = startRowPosition;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#setTooManySearchReturn(boolean)
	 */
	@Override
	public void setTooManySearchReturn(boolean tooManySearchReturn) {
		this.tooManySearchReturn = tooManySearchReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.filter.Page#setTotalRecords(int)
	 */
	@Override
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Total records: ").append(totalRecords)
				.append(", Records/page: ").append(recordsPerPage)
				.append(", Current page: ").append(currentPage);
		return sb.toString();
	}
}

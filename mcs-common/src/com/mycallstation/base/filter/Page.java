/**
 * 
 */
package com.mycallstation.base.filter;

/**
 * @author Jack
 * 
 */
public interface Page {
	public static int RECORDS_PER_PAGE = 10;

	public int getStartRowPosition();

	public void setStartRowPosition(int startRowPosition);

	public int getEndRowPosition();

	public void setTotalRecords(int totalRecords);

	public int getTotalRecords();

	public boolean isTooManySearchReturn();

	public void setTooManySearchReturn(boolean tooManySearchReturn);

	public void setRecordsPerPage(int records);

	public int getRecordsPerPage();

	public void setCurrentPage(int currentPage);

	public int getCurrentPage();

	public int getTotalPages();
}

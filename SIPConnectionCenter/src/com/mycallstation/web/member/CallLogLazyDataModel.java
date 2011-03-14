/**
 * 
 */
package com.mycallstation.web.member;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.primefaces.model.LazyDataModel;

import com.mycallstation.base.filter.FSP;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.FilterFactory;
import com.mycallstation.base.filter.Page;
import com.mycallstation.base.filter.Sort;
import com.mycallstation.base.filter.Sort.Direction;
import com.mycallstation.common.model.User;
import com.mycallstation.sip.CallStatus;
import com.mycallstation.sip.CallType;
import com.mycallstation.sip.business.CallLogService;
import com.mycallstation.sip.model.CallLog;
import com.mycallstation.web.util.JSFUtils;

/**
 * @author wgao
 * 
 */
public class CallLogLazyDataModel extends LazyDataModel<CallLog> {
	private static final long serialVersionUID = 6187662282080590301L;

	private transient CallLogService callLogService;

	private transient FilterFactory filterFactory;

	private Filter baseFilter;

	public CallLogLazyDataModel() {
		super();
	}

	public void init(User user, Date startDate, Date endDate,
			boolean includeInConnected, boolean includeInFailed,
			boolean includeInCanceled, boolean includeOutConnected,
			boolean includeOutFailed, boolean includeOutCanceled) {
		if (user == null) {
			throw new NullPointerException("User is required.");
		}
		if (startDate == null) {
			throw new NullPointerException("Start date is required.");
		}
		if (!includeInConnected && !includeInFailed && !includeInCanceled
				&& !includeOutConnected && !includeOutFailed
				&& !includeOutCanceled) {
			throw new IllegalArgumentException("None data selected");
		}
		FilterFactory filterFactory = getFilterFactory();
		Filter filter = filterFactory.createSimpleFilter("owner.id",
				user.getId());
		Date ed = endDate;
		if (endDate == null) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			ed = c.getTime();
		}
		Filter f = filterFactory
				.createBetweenFilter("startTime", startDate, ed);
		filter = filter.appendAnd(f);

		if (!includeInConnected || !includeInFailed || !includeInCanceled
				|| !includeOutConnected || !includeOutFailed
				|| !includeOutCanceled) {
			Filter ff = null;
			if (includeInConnected || includeInFailed || includeInCanceled) {
				f = filterFactory.createSimpleFilter("type", CallType.INCOMING);
				if (!includeInConnected || !includeInFailed
						|| !includeInCanceled) {
					Filter f1 = null;
					if (includeInConnected) {
						f1 = filterFactory.createSimpleFilter("status",
								CallStatus.SUCCESS);
					}
					if (includeInFailed) {
						if (f1 == null) {
							f1 = filterFactory.createSimpleFilter("status",
									CallStatus.FAILED);
						} else {
							f1 = f1.appendOr(filterFactory.createSimpleFilter(
									"status", CallStatus.FAILED));
						}
					}
					if (includeInCanceled) {
						if (f1 == null) {
							f1 = filterFactory.createSimpleFilter("status",
									CallStatus.CANCELED);
						} else {
							f1 = f1.appendOr(filterFactory.createSimpleFilter(
									"status", CallStatus.CANCELED));
						}
					}
					f = f.appendAnd(f1);
				}
				ff = f;
			}

			if (includeOutConnected || includeOutFailed || includeOutCanceled) {
				f = filterFactory.createSimpleFilter("type", CallType.OUTGOING);
				if (!includeOutConnected || !includeOutFailed
						|| !includeOutCanceled) {
					Filter f1 = null;
					if (includeOutConnected) {
						f1 = filterFactory.createSimpleFilter("status",
								CallStatus.SUCCESS);
					}
					if (includeOutFailed) {
						if (f1 == null) {
							f1 = filterFactory.createSimpleFilter("status",
									CallStatus.FAILED);
						} else {
							f1 = f1.appendOr(filterFactory.createSimpleFilter(
									"status", CallStatus.FAILED));
						}
					}
					if (includeOutCanceled) {
						if (f1 == null) {
							f1 = filterFactory.createSimpleFilter("status",
									CallStatus.CANCELED);
						} else {
							f1 = f1.appendOr(filterFactory.createSimpleFilter(
									"status", CallStatus.CANCELED));
						}
					}
					f = f.appendAnd(f1);
				}
				if (ff == null) {
					ff = f;
				} else {
					ff = ff.appendOr(f);
				}
			}

			if (ff != null) {
				filter = filter.appendAnd(ff);
			}
		}

		baseFilter = filter;
		setRowCount(getCallLogService().getRowCount(baseFilter));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#load(int, int, java.lang.String,
	 * boolean, java.util.Map)
	 */
	@Override
	public List<CallLog> load(int first, int pageSize, String sortField,
			boolean sortOrder, Map<String, String> filters) {
		Filter filter = baseFilter;
		if (filters != null) {
			for (Entry<String, String> entry : filters.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key != null && value != null) {
					Filter f1 = getFilterFactory().createSimpleFilter(key,
							"%" + value + "%", Filter.Operator.ILIKE);
					filter = filter.appendAnd(f1);
				}
			}
		}
		Page page = getFilterFactory().createPage();
		page.setStartRowPosition(first);
		page.setRecordsPerPage(pageSize);
		Sort sort = null;
		if (sortField != null) {
			sort = getFilterFactory().createSort(sortField,
					sortOrder ? Direction.ASC : Direction.DESC);
		} else {
			sort = getFilterFactory().createSort("startTime", Direction.DESC);
		}
		FSP fsp = new FSP();
		fsp.setFilter(filter);
		fsp.setPage(page);
		fsp.setSort(sort);
		List<CallLog> callLogs = getCallLogService().getEntities(fsp);
		setRowCount(page.getTotalRecords());
		return callLogs;
	}

	private CallLogService getCallLogService() {
		if (callLogService == null) {
			callLogService = JSFUtils.getManagedBean("callLogService",
					CallLogService.class);
		}
		return callLogService;
	}

	private FilterFactory getFilterFactory() {
		if (filterFactory == null) {
			filterFactory = JSFUtils.getManagedBean("filterFactory",
					FilterFactory.class);
		}
		return filterFactory;
	}
}

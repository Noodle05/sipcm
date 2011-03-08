/**
 * 
 */
package com.sipcm.web.member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.primefaces.model.LazyDataModel;

import com.sipcm.base.filter.FSP;
import com.sipcm.base.filter.Filter;
import com.sipcm.base.filter.FilterFactory;
import com.sipcm.base.filter.Page;
import com.sipcm.base.filter.Sort;
import com.sipcm.base.filter.Sort.Direction;
import com.sipcm.common.model.User;
import com.sipcm.sip.CallStatus;
import com.sipcm.sip.CallType;
import com.sipcm.sip.business.CallLogService;
import com.sipcm.sip.model.CallLog;
import com.sipcm.web.util.JSFUtils;

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
			boolean includeConnected, boolean includeFailed,
			boolean includeCancelled, String callType) {
		if (user == null) {
			throw new NullPointerException("User is required.");
		}
		if (startDate == null) {
			throw new NullPointerException("Start date is required.");
		}
		if (!includeConnected && !includeFailed && !includeCancelled) {
			throw new IllegalArgumentException("None data selected");
		}
		FilterFactory filterFactory = getFilterFactory();
		Filter filter = filterFactory.createSimpleFilter("owner.owner", user);
		if (endDate != null) {
			Filter f = filterFactory.createBetweenFilter("startTime",
					startDate, endDate);
			filter = filter.appendAnd(f);
		} else {
			Filter f = filterFactory.createSimpleFilter("startTime", startDate,
					Filter.Operator.GREATER_EQ);
			filter = filter.appendAnd(f);
		}
		List<CallStatus> cs = new ArrayList<CallStatus>();
		if (includeConnected) {
			cs.add(CallStatus.SUCCESS);
		}
		if (includeFailed) {
			cs.add(CallStatus.FAILED);
		}
		if (includeCancelled) {
			cs.add(CallStatus.CANCELLED);
		}
		if (cs.size() > 0) {
			if (cs.size() == 1) {
				Filter f = filterFactory.createSimpleFilter("status", cs
						.iterator().next());
				filter = filter.appendAnd(f);
			} else if (cs.size() < 3) {
				Collection<CallStatus> css = new ArrayList<CallStatus>(
						Arrays.asList(CallStatus.values()));
				css.removeAll(cs);
				Filter f = filterFactory.createSimpleFilter("status", css
						.iterator().next(), Filter.Operator.NOT_EQ);
				filter = filter.appendAnd(f);
			}
		}

		if (CallLogBean.CALLTYPE_INCOMING.equalsIgnoreCase(callType)) {
			Filter f = filterFactory.createSimpleFilter("type",
					CallType.INCOMING);
			filter = filter.appendAnd(f);
		} else if (CallLogBean.CALLTYPE_OUTGOING.equalsIgnoreCase(callType)) {
			Filter f = filterFactory.createSimpleFilter("type",
					CallType.OUTGOING);
			filter = filter.appendAnd(f);
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
							value + "%", Filter.Operator.ILIKE);
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
		return getCallLogService().getEntities(fsp);
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

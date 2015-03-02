/**
 * 
 */
package com.mycallstation.web.member;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mycallstation.base.filter.Direction;
import com.mycallstation.base.filter.FSP;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.FilterFactory;
import com.mycallstation.base.filter.Operator;
import com.mycallstation.base.filter.Page;
import com.mycallstation.base.filter.Sort;
import com.mycallstation.constant.CallStatus;
import com.mycallstation.constant.CallType;
import com.mycallstation.dataaccess.business.CallLogService;
import com.mycallstation.dataaccess.model.CallLog;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.web.util.JSFUtils;

/**
 * @author Wei Gao
 * 
 */
@Component("callLogLazyDataModel")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CallLogLazyDataModel extends LazyDataModel<CallLog> {
    private static final long serialVersionUID = 6187662282080590301L;

    private static final Logger logger = LoggerFactory
            .getLogger(CallLogLazyDataModel.class);

    private Filter baseFilter;

    @Resource(name = "filterFactory")
    private FilterFactory filterFactory;

    @Resource(name = "callLogService")
    private CallLogService callLogService;

    @Resource(name = "jsfUtils")
    private JSFUtils jsfUtils;

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
        Filter filter = filterFactory.createSimpleFilter("owner.id",
                user.getId());
        Calendar c;
        if (endDate == null) {
            c = Calendar.getInstance(jsfUtils.getCurrentTimeZone());
        } else {
            c = Calendar.getInstance(jsfUtils.getCurrentTimeZone());
            c.setTime(endDate);
        }
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date ed = c.getTime();
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
        setRowCount(callLogService.getRowCount(baseFilter));
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
        logger.debug(
                "Loadding call log with filter: {}, first: {}, page size: {}, sort field: {}",
                filters, first, pageSize, sortField);
        Filter filter = baseFilter;
        if (filters != null) {
            for (Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    Filter f1 = filterFactory.createSimpleFilter(key, "%"
                            + value + "%", Operator.ILIKE);
                    filter = filter.appendAnd(f1);
                }
            }
        }
        Page page = filterFactory.createPage();
        page.setStartRowPosition(first);
        page.setRecordsPerPage(pageSize);
        Sort sort = null;
        if (sortField != null) {
            sort = filterFactory.createSort(sortField,
                    sortOrder ? Direction.ASC : Direction.DESC);
        } else {
            sort = filterFactory.createSort("startTime", Direction.DESC);
        }
        FSP fsp = new FSP();
        fsp.setFilter(filter);
        fsp.setPage(page);
        fsp.setSort(sort);
        List<CallLog> callLogs = callLogService.getEntities(fsp);
        setRowCount(page.getTotalRecords());
        return callLogs;
    }
}

/**
 * 
 */
package com.mycallstation.base.filter.impl;

import com.mycallstation.base.filter.Filter;

/**
 * @author Wei Gao
 * 
 */
abstract class BaseFilter implements Filter {
    private static final String PARAM_NAME_PREFIX = "param_";

    @Override
    public Filter appendAnd(Filter filter) {
        Filter ret = this;
        if (filter != null) {
            ret = new AndOrFilter(this, filter, true);
        }
        return ret;
    }

    @Override
    public String getString() {
        return getString(new PositionHolder());
    }

    protected abstract String getString(PositionHolder positionHolder);

    @Override
    public Filter appendOr(Filter filter) {
        Filter ret = this;
        if (filter != null) {
            ret = new AndOrFilter(this, filter, false);
        }
        return ret;
    }

    @Override
    public String toString() {
        return getString();
    }

    protected String getParameterName(PositionHolder positionHolder) {
        StringBuilder sb = new StringBuilder(":");
        sb.append(PARAM_NAME_PREFIX);
        sb.append(positionHolder.getPosition());
        return sb.toString();
    }
}

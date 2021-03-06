/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.Operator;

/**
 * @author Wei Gao
 * 
 */
class ExistFilter extends BaseFilter implements Serializable {
    private static final long serialVersionUID = -8938735745979348763L;

    private final StringFilter selectQuery;

    private final Operator operator;

    ExistFilter(StringFilter selectQuery, boolean notFlag) {
        this.selectQuery = selectQuery;
        operator = notFlag ? Operator.NOT_EXISTS : Operator.EXISTS;
    }

    public Filter getLeftHand() {
        return null;
    }

    public Filter getRightHand() {
        return null;
    }

    @Override
    protected String getString(PositionHolder positionHolder) {
        StringBuilder sb = new StringBuilder();
        sb.append(operator.getString()).append(" ")
                .append(selectQuery.getString(positionHolder));
        return sb.toString();
    }

    @Override
    public List<Serializable> getValues() {
        return Collections.emptyList();
    }
}

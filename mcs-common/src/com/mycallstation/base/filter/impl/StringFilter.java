/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Wei Gao
 * 
 */
class StringFilter extends BaseFilter implements Serializable {
    private static final long serialVersionUID = 8168643045904058467L;

    private final String strCondition;

    StringFilter(String condition) {
        this.strCondition = condition;
    }

    @Override
    protected String getString(PositionHolder positionHolder) {
        return strCondition;
    }

    @Override
    public List<Serializable> getValues() {
        return Collections.emptyList();
    }
}

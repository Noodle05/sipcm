/**
 * 
 */
package com.mycallstation.base.filter.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.Operator;

/**
 * @author Wei Gao
 * 
 */
class InListFilter extends BaseInFilter implements Serializable {
    private static final long serialVersionUID = 910431459453687700L;

    private final ArrayList<? extends Serializable> rightHand;

    <T extends Serializable> InListFilter(String left, List<T> right,
            boolean notFlag) {
        super(left, notFlag);
        this.rightHand = new ArrayList<>(right);
    }

    @Override
    protected String getString(PositionHolder positionHolder) {
        StringBuilder sb = new StringBuilder();
        if (rightHand == null || rightHand.isEmpty()) {
            sb.append(Operator.NOT_IN.equals(operator) ? "1 = 1" : "0 = 1");
        } else {
            sb.append(Filter.DEFAULT_ALIAS).append(".").append(leftHand)
                    .append(" ").append(operator.getString()).append(" (")
                    .append(getParameterName(positionHolder)).append(")");
        }
        return sb.toString();
    }

    @Override
    public List<Serializable> getValues() {
        List<Serializable> ret = new ArrayList<Serializable>(1);
        ret.add(rightHand);
        return ret;
    }
}

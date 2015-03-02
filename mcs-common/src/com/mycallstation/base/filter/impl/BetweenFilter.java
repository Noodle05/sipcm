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
class BetweenFilter extends BaseFilter implements Serializable {
    private static final long serialVersionUID = 9049839710701185793L;

    private final String leftHand;

    private final Serializable rightHand1;

    private final Serializable rightHand2;

    private final Operator operator;

    BetweenFilter(String left, Serializable right1, Serializable right2,
            boolean notFlag) {
        this.leftHand = left;
        this.rightHand1 = right1;
        this.rightHand2 = right2;
        operator = notFlag ? Operator.NOT_BETWEEN : Operator.BETWEEN;
    }

    @Override
    protected String getString(PositionHolder positionHolder) {
        StringBuilder sb = new StringBuilder();
        sb.append(Filter.DEFAULT_ALIAS).append(".").append(leftHand)
                .append(" ").append(operator.getString()).append(" ")
                .append(getParameterName(positionHolder)).append(" and ")
                .append(getParameterName(positionHolder));
        return sb.toString();
    }

    @Override
    public List<Serializable> getValues() {
        List<Serializable> ret = new ArrayList<Serializable>(2);
        ret.add(rightHand1);
        ret.add(rightHand2);
        return ret;
    }
}

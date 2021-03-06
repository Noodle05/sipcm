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
class MemberOfFilter extends BaseFilter implements Serializable {
    private static final long serialVersionUID = 685227069924405650L;

    private final String leftHand;

    private final Serializable rightHand;

    private final Operator operator;

    MemberOfFilter(String name, Serializable value, boolean notFlag) {
        this.leftHand = name;
        this.rightHand = value;
        this.operator = notFlag ? Operator.NOT_MEMBER_OF : Operator.MEMBER_OF;
    }

    @Override
    protected String getString(PositionHolder positionHolder) {
        StringBuilder sb = new StringBuilder();
        sb.append(getParameterName(positionHolder)).append(" ")
                .append(operator.getString()).append(" ")
                .append(Filter.DEFAULT_ALIAS).append(".").append(leftHand);
        return sb.toString();
    }

    @Override
    public List<Serializable> getValues() {
        List<Serializable> ret = new ArrayList<Serializable>(1);
        ret.add(rightHand);
        return ret;
    }
}

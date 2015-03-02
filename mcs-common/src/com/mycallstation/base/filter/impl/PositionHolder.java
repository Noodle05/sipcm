/**
 * 
 */
package com.mycallstation.base.filter.impl;

/**
 * @author Wei Gao
 */
class PositionHolder {
    private int val;
    
    PositionHolder() {
        val = 1;
    }
    
    int getPosition() {
        return val ++;
    }
}

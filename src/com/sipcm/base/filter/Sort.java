/**
 * 
 */
package com.sipcm.base.filter;

/**
 * @author Jack
 * 
 */
public interface Sort {
	public static enum Direction {
		ASC, DESC
	};

	public Sort appendSort(Sort next);
}

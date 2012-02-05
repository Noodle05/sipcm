/**
 * 
 */
package com.mycallstation.events;

import java.io.Serializable;

/**
 * @author Wei Gao
 * 
 */
public class ServiceEvent implements Serializable {
	private static final long serialVersionUID = 6885382284779493427L;

	private final Operation operation;
	private final Long[] ids;

	public ServiceEvent(Operation operation, Long[] ids) {
		this.operation = operation;
		this.ids = ids;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * @return the ids
	 */
	public Long[] getIds() {
		return ids;
	}
}

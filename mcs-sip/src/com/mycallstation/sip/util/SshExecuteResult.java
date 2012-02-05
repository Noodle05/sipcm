/**
 * 
 */
package com.mycallstation.sip.util;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Wei Gao
 * 
 */
public class SshExecuteResult implements Serializable {
	private static final long serialVersionUID = -4284097179107314460L;

	private final int exitStatus;
	private final Collection<String> output;
	private final Collection<String> error;

	public SshExecuteResult(int exitStatus, Collection<String> output,
			Collection<String> error) {
		this.exitStatus = exitStatus;
		this.output = output;
		this.error = error;
	}

	public int getExitStatus() {
		return exitStatus;
	}

	public Collection<String> getOutput() {
		return output;
	}

	public Collection<String> getError() {
		return error;
	}
}

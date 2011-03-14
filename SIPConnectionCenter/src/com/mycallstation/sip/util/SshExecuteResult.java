/**
 * 
 */
package com.mycallstation.sip.util;

import java.util.Collection;

/**
 * @author wgao
 * 
 */
public class SshExecuteResult {
	private int exitStatus;
	private Collection<String> output;
	private Collection<String> error;

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

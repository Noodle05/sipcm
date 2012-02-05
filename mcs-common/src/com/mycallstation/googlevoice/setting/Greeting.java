/**
 * 
 */
package com.mycallstation.googlevoice.setting;

import java.io.Serializable;

/**
 * @author Wei Gao
 * 
 */
public class Greeting implements Serializable {
	private static final long serialVersionUID = 1842693027804173964L;
	private int id;
	private String name;
	private String jobberName;

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param jobberName
	 *            the jobberName to set
	 */
	public void setJobberName(String jobberName) {
		this.jobberName = jobberName;
	}

	/**
	 * @return the jobberName
	 */
	public String getJobberName() {
		return jobberName;
	}
}

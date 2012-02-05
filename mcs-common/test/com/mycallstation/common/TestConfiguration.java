/**
 * 
 */
package com.mycallstation.common;

import java.io.File;

/**
 * @author Wei Gao
 * 
 */
public class TestConfiguration extends BaseConfiguration {
	public String getDomain() {
		return "mycallstation.com";
	}

	public String getGoogleAuthenticationAppname() {
		return "MyCallStation-1.0";
	}

	public File getTemperoryFolder() {
		return new File("/Volumes/Users/wgao/tmp/");
	}

	public int getHttpClientConnectionTimeout() {
		return 5000;
	}
}

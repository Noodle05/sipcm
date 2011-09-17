/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.Serializable;

import javax.servlet.sip.ServletTimer;

/**
 * @author wgao
 * 
 */
public interface TimerProcessor extends Serializable {
	public void timeout(ServletTimer timer);
}

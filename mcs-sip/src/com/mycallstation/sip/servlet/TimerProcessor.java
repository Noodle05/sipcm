/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.Serializable;

import javax.servlet.sip.ServletTimer;

/**
 * @author Wei Gao
 * 
 */
public interface TimerProcessor extends Serializable {
	public void timeout(ServletTimer timer);
}

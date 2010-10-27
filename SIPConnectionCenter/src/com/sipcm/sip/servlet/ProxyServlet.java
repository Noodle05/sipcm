/**
 * 
 */
package com.sipcm.sip.servlet;

import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "ProxyServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class ProxyServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 4482632014711350427L;

}

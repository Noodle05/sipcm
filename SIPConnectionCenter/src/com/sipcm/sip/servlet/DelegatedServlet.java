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
@SipServlet(name = "DelegatedServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class DelegatedServlet extends B2bServlet {
	private static final long serialVersionUID = 5263932525339104271L;

}

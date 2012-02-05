/**
 * 
 */
package com.mycallstation.web;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Wei Gao
 * 
 */
@Component("homePage")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class HomePageBean implements Serializable {
	private static final long serialVersionUID = 8421390516129157988L;

	public void initSession() {
		FacesContext.getCurrentInstance().getExternalContext().getSession(true);
	}
}

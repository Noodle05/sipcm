/**
 * 
 */
package com.mycallstation.web;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "homePage")
@RequestScoped
public class HomePageBean implements Serializable {
	private static final long serialVersionUID = 8421390516129157988L;

	public void initSession() {
		FacesContext.getCurrentInstance().getExternalContext().getSession(true);
	}
}

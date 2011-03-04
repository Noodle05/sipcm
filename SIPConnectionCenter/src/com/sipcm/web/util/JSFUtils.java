/**
 * 
 */
package com.sipcm.web.util;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;

/**
 * @author wgao
 * 
 */
public abstract class JSFUtils {
	public static <T> T getManagedBean(String managedBeanKey, Class<T> clazz)
			throws IllegalArgumentException {
		if (managedBeanKey == null) {
			throw new NullPointerException("Managed Bean Key is null.");
		}
		if (managedBeanKey.isEmpty()) {
			throw new IllegalArgumentException("Managed Bean key is empty.");
		}
		if (clazz == null) {
			throw new NullPointerException("Class is null.");
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null) {
			return null;
		}
		ELResolver resolver = facesContext.getApplication().getELResolver();
		ELContext elContext = facesContext.getELContext();
		Object managedBean = resolver.getValue(elContext, null, managedBeanKey);
		if (!elContext.isPropertyResolved()) {
			throw new IllegalArgumentException(
					"No managed bean found for key: " + managedBeanKey);
		}
		if (managedBean == null) {
			return null;
		} else {
			if (clazz.isInstance(managedBean)) {
				return clazz.cast(managedBean);
			} else {
				throw new IllegalArgumentException(
						"Managed bean is not of type [" + clazz.getName()
								+ "] | Actual type is: ["
								+ managedBean.getClass().getName() + "]");
			}
		}
	}
}

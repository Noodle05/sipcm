/**
 * 
 */
package com.sipcm.spring;

import java.util.Collection;

import javax.persistence.Entity;

import com.sipcm.util.ClassUtils;
import org.hibernate.HibernateException;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.cfg.AnnotationConfiguration;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

/**
 * @author jack
 * 
 */
@SuppressWarnings("deprecation")
public class LocalAnnotationSessionFactoryBean extends
		AnnotationSessionFactoryBean {
	private String basePackage;

	/**
	 * @param basePackage
	 *            the basePackage to set
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * @return the basePackage
	 */
	public String getBasePackage() {
		return basePackage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean
	 * #
	 * postProcessAnnotationConfiguration(org.hibernate.cfg.AnnotationConfiguration
	 * )
	 */
	@Override
	protected void postProcessAnnotationConfiguration(
			AnnotationConfiguration config) throws HibernateException {
		Collection<Class<?>> clazzes = ClassUtils.findClasses(basePackage,
				Entity.class);

		Collection<Package> packages = ClassUtils.findPackages(basePackage,
				TypeDefs.class);
		for (Package p : packages) {
			config.addPackage(p.getName());
		}

		for (Class<?> clazz : clazzes) {
			config.addAnnotatedClass(clazz);
		}
	}
}

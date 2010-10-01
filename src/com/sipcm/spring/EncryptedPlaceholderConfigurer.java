/**
 * 
 */
package com.sipcm.spring;

import java.security.Provider;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.sipcm.util.CodecTool;

/**
 * @author jack
 * 
 */
public class EncryptedPlaceholderConfigurer extends
		PropertyPlaceholderConfigurer {
	private CodecTool cipherTool;

	private Provider provider;

	private String algorithm;

	public static final String ENCRYPTED_PROPERTIES_SUFFIX = ".encrypted";

	public void init() {
		cipherTool = CodecTool.getInstance(provider, algorithm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.springframework.beans.factory.config.PropertyResourceConfigurer#
	 * convertProperties(java.util.Properties)
	 */
	@Override
	protected void convertProperties(Properties props) {
		Enumeration<?> propertyNames = props.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String propertyName = (String) propertyNames.nextElement();
			String propertyValue = props.getProperty(propertyName);
			String convertedValue = convertPropertyValue(propertyValue);
			if (propertyName.endsWith(ENCRYPTED_PROPERTIES_SUFFIX)) {
				try {
					convertedValue = cipherTool.decrypt(convertedValue);
				} catch (Exception exp) {
					if (logger.isWarnEnabled()) {
						logger.warn(
								"Cannot decrypt property, is it a encrypted property? Property name: "
										+ propertyName + ", encrypted value: "
										+ convertedValue
										+ ", use original value.", exp);
					}
				}
			}
			if (!ObjectUtils.equals(propertyValue, convertedValue)) {
				props.setProperty(propertyName, convertedValue);
			}
		}
	}

	/**
	 * @param provider
	 *            the provider to set
	 */
	@Required
	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	/**
	 * @param algorithm
	 *            the algorithm to set
	 */
	@Required
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
}

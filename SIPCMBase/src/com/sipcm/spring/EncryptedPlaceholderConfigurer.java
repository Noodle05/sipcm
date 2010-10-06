/**
 * 
 */
package com.sipcm.spring;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.sipcm.util.CodecTool;

/**
 * @author jack
 * 
 */
public class EncryptedPlaceholderConfigurer extends
		PropertyPlaceholderConfigurer {
	private TextEncryptor textEncryptor;

	public static final String ENCRYPTED_PROPERTIES_SUFFIX = ".encrypted";

	public void init() {
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword(CodecTool.PASSWORD);
		textEncryptor = encryptor;
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
			if (propertyName.endsWith(ENCRYPTED_PROPERTIES_SUFFIX)
					&& StringUtils.isNotBlank(convertedValue)) {
				try {
					convertedValue = textEncryptor.decrypt(convertedValue);
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
}

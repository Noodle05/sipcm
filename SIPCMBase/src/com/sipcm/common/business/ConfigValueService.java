/**
 * 
 */
package com.sipcm.common.business;

import com.sipcm.base.business.Service;
import com.sipcm.common.model.ConfigValue;

/**
 * @author Jack
 * 
 */
public interface ConfigValueService extends Service<ConfigValue, Integer> {
	public ConfigValue getValueByKey(String key);

	public ConfigValue removeValueByKey(String key);

	public void removeAll();
}

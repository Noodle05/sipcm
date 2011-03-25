/**
 * 
 */
package com.mycallstation.dataaccess.business;

import com.mycallstation.base.business.Service;
import com.mycallstation.dataaccess.model.ConfigValue;

/**
 * @author Jack
 * 
 */
public interface ConfigValueService extends Service<ConfigValue, Integer> {
	public ConfigValue getValueByKey(String key);

	public ConfigValue removeValueByKey(String key);

	public void removeAll();
}

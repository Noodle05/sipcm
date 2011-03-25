/**
 * 
 */
package com.mycallstation.events;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wgao
 * 
 */
public interface MessageDelegate {
	void handleMessage(String message);

	void handleMessage(Map<?, ?> message);

	void handleMessage(byte[] message);

	void handleMessage(Serializable message);
}

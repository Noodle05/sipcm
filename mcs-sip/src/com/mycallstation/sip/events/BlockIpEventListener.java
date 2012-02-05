/**
 * 
 */
package com.mycallstation.sip.events;

import java.util.EventListener;

/**
 * @author Wei Gao
 * 
 */
public interface BlockIpEventListener extends EventListener {
	public void blockIp(BlockIpEvent event);

	public void unblockIp(BlockIpEvent event);
}

/**
 * 
 */
package com.sipcm.sip.events;

import java.util.EventListener;

/**
 * @author wgao
 * 
 */
public interface BlockIpEventListener extends EventListener {
	public void blockIp(BlockIpEvent event);

	public void unblockIp(BlockIpEvent event);
}

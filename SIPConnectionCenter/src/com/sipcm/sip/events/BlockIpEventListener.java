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
	public void blockIp(BlockIpEventObject event);

	public void unblockIp(BlockIpEventObject event);
}

/**
 * 
 */
package com.sipcm.base.model;

import java.util.Date;

/**
 * @author Jack
 * 
 */
public interface ExpireableEntity extends TimestampBasedEntity {
	public Date getExpireDate();
}

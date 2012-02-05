/**
 * 
 */
package com.mycallstation.base.model;

import java.util.Date;

/**
 * @author Wei Gao
 * 
 */
public interface ExpireableEntity extends TimestampBasedEntity {
	public Date getExpireDate();
}

/**
 * 
 */
package com.mycallstation.sip.util;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.model.AddressBinding;

/**
 * @author Wei Gao
 * 
 */
@Component("sipAddressComparator")
public class SipAddressComparator implements Comparator<AddressBinding> {
	@Override
	public int compare(AddressBinding o1, AddressBinding o2) {
		if (o1 == null) {
			if (o2 != null) {
				return -1;
			} else {
				return 0;
			}
		}
		if (o2 == null) {
			return 1;
		}
		int expiresTime1 = o1.getExpires();
		int expiresTime2 = o2.getExpires();
		if (expiresTime1 < expiresTime2) {
			return 1;
		} else if (expiresTime1 > expiresTime2) {
			return -1;
		}
		return 0;
	}
}

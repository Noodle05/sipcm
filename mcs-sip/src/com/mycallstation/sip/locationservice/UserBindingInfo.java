/**
 * 
 */
package com.mycallstation.sip.locationservice;

import java.io.Serializable;
import java.util.Collection;

import com.mycallstation.dataaccess.model.AddressBinding;
import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public class UserBindingInfo implements Serializable {
	private static final long serialVersionUID = 6937366901196023863L;

	private final UserVoipAccount account;
	private final Collection<AddressBinding> bindings;

	public UserBindingInfo(UserVoipAccount account,
			Collection<AddressBinding> bindings) {
		if (bindings == null) {
			throw new NullPointerException("Bindings cannot be null.");
		}
		if (bindings.isEmpty()) {
			throw new IllegalArgumentException("Bindings cannot be empty.");
		}
		this.account = account;
		this.bindings = bindings;
	}

	/**
	 * @return the account
	 */
	public UserVoipAccount getAccount() {
		return account;
	}

	/**
	 * @return the bindings
	 */
	public Collection<AddressBinding> getBindings() {
		return bindings;
	}
}

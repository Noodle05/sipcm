/**
 * 
 */
package com.sipcm.sip.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.sipcm.base.model.IdBasedEntity;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_usersipbinding")
public class UserSipBinding implements Serializable, IdBasedEntity<Long> {
	private static final long serialVersionUID = -8933161937827326034L;
	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "userSipProfile"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@OneToOne
	@PrimaryKeyJoinColumn
	private UserSipProfile userSipProfile;

	@Basic
	@Column(name = "address", length = 255, nullable = false, unique = true)
	private String addressOfRecord;

	@OneToMany(mappedBy = "userSipBinding", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//	@Sort(type = SortType.NATURAL)
	@Fetch(FetchMode.SUBSELECT)
	private Set<AddressBinding> bindings;

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.model.IdBasedEntity#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @param userSipProfile
	 *            the userSipProfile to set
	 */
	public void setUserSipProfile(UserSipProfile userSipProfile) {
		this.userSipProfile = userSipProfile;
	}

	/**
	 * @return the userSipProfile
	 */
	public UserSipProfile getUserSipProfile() {
		return userSipProfile;
	}

	/**
	 * @param addressOfRecord
	 *            the addressOfRecord to set
	 */
	public void setAddressOfRecord(String addressOfRecord) {
		this.addressOfRecord = addressOfRecord;
	}

	/**
	 * @return the addressOfRecord
	 */
	public String getAddressOfRecord() {
		return addressOfRecord;
	}

	/**
	 * @param bindings
	 *            the bindings to set
	 */
	public void setBindings(Set<AddressBinding> bindings) {
		this.bindings = bindings;
	}

	/**
	 * @return the bindings
	 */
	public Set<AddressBinding> getBindings() {
		return bindings;
	}

	@Override
	public int hashCode() {
		final int prime = 105;
		int result = 7;
		result = prime * result
				+ ((userSipProfile == null) ? 0 : userSipProfile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof UserSipBinding)) {
			return false;
		}
		final UserSipBinding obj = (UserSipBinding) other;
		if (userSipProfile == null) {
			if (obj.userSipProfile != null) {
				return false;
			}
		} else if (!(userSipProfile.equals(obj.userSipProfile))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UserSipBinding["
				+ (userSipProfile == null ? "" : userSipProfile.toString())
				+ "]";
	}
}

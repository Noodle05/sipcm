/**
 * 
 */
package com.sipcm.web.member;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.security.UserDetailsImpl;
import com.sipcm.sip.VoipAccountType;
import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.business.VoipVendorService;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;
import com.sipcm.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "voipAccountSettingBean")
@SessionScoped
public class VoipAccountSettingBean implements Serializable {
	private static final long serialVersionUID = -4107844991260539763L;

	@ManagedProperty(value = "#{voipVendorService}")
	private transient VoipVendorService voipVendorService;

	@ManagedProperty(value = "#{userSipProfileService}")
	private transient UserSipProfileService userSipProfileService;

	@ManagedProperty(value = "#{userService}")
	private transient UserService userService;

	@ManagedProperty(value = "#{userVoipAccountService}")
	private transient UserVoipAccountService userVoipAccountService;

	private User user;
	private UserSipProfile userSipProfile;

	private Collection<UserVoipAccount> voipAccounts;

	private String sipProfilePhoneNumber = "";

	private String sipProfileDefaultArea = "";

	private boolean sipProfileAllowInternal = true;

	@PostConstruct
	public void init() {
		user = getCurrentUser();
		if (user == null) {
			throw new IllegalStateException(
					"Access this page without user? Can't be!");
		}
		userSipProfile = getUserSipProfile();
		if (userSipProfile != null) {
			sipProfilePhoneNumber = userSipProfile.getPhoneNumber();
			sipProfileDefaultArea = userSipProfile.getDefaultAreaCode();
			sipProfileAllowInternal = userSipProfile.isAllowLocalDirectly();
			voipAccounts = userVoipAccountService
					.getUserVoipAccount(userSipProfile);
		}
	}

	public void saveSipProfile() {
		if (userSipProfile == null) {
			userSipProfile = userSipProfileService.createUserSipProfile(user);
		}
		userSipProfile.setPhoneNumber(sipProfilePhoneNumber);
		userSipProfile.setDefaultAreaCode(sipProfileDefaultArea);
		userSipProfile.setAllowLocalDirectly(sipProfileAllowInternal);
		userSipProfileService.saveEntity(userSipProfile);
		FacesMessage message = Messages.getMessage("member.sipprofile.success",
				FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void accountRowEdit(RowEditEvent event) {
		@SuppressWarnings("unused")
		UserVoipAccount account = (UserVoipAccount) event.getObject();
		FacesMessage message = Messages
				.getMessage("member.voip.accounts.save.success",
						FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * @param voipAccounts
	 *            the voipAccounts
	 */
	public void setVoipAccounts(Collection<UserVoipAccount> voipAccounts) {
		this.voipAccounts = voipAccounts;
	}

	/**
	 * @return the voipAccounts
	 */
	public Collection<UserVoipAccount> getVoipAccounts() {
		return voipAccounts;
	}

	public Collection<VoipVendor> getVoipVendors() {
		return voipVendorService.getManagableVoipVendors();
	}

	public VoipAccountType[] getVoipAccountTypes() {
		return VoipAccountType.values();
	}

	private UserSipProfile getUserSipProfile() {
		userSipProfile = userSipProfileService.getUserSipProfileByUser(user);
		return userSipProfile;
	}

	private User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		User user = null;
		if (principal instanceof UserDetailsImpl) {
			user = ((UserDetailsImpl) principal).getUser();
		} else {
			String username = principal.toString();
			user = userService.getUserByUsername(username);
		}
		return user;
	}

	public boolean isHasSipProfile() {
		return userSipProfile != null;
	}

	/**
	 * @param voipVendorService
	 *            the voipVendorService to set
	 */
	public void setVoipVendorService(VoipVendorService voipVendorService) {
		this.voipVendorService = voipVendorService;
	}

	/**
	 * @param userSipProfileService
	 *            the userSipProfileService to set
	 */
	public void setUserSipProfileService(
			UserSipProfileService userSipProfileService) {
		this.userSipProfileService = userSipProfileService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @param userVoipAccountService
	 *            the userVoipAccountService to set
	 */
	public void setUserVoipAccountService(
			UserVoipAccountService userVoipAccountService) {
		this.userVoipAccountService = userVoipAccountService;
	}

	/**
	 * @param sipProfilePhoneNumber
	 *            the sipProfilePhoneNumber to set
	 */
	public void setSipProfilePhoneNumber(String sipProfilePhoneNumber) {
		this.sipProfilePhoneNumber = sipProfilePhoneNumber;
	}

	/**
	 * @return the sipProfilePhoneNumber
	 */
	public String getSipProfilePhoneNumber() {
		return sipProfilePhoneNumber;
	}

	/**
	 * @param sipProfileDefaultArea
	 *            the sipProfileDefaultArea to set
	 */
	public void setSipProfileDefaultArea(String sipProfileDefaultArea) {
		this.sipProfileDefaultArea = sipProfileDefaultArea;
	}

	/**
	 * @return the sipProfileDefaultArea
	 */
	public String getSipProfileDefaultArea() {
		return sipProfileDefaultArea;
	}

	/**
	 * @param sipProfileAllowInternal
	 *            the sipProfileAllowInternal to set
	 */
	public void setSipProfileAllowInternal(boolean sipProfileAllowInternal) {
		this.sipProfileAllowInternal = sipProfileAllowInternal;
	}

	/**
	 * @return the sipProfileAllowInternal
	 */
	public boolean isSipProfileAllowInternal() {
		return sipProfileAllowInternal;
	}
}

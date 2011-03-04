/**
 * 
 */
package com.sipcm.web.member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.security.UserDetailsImpl;
import com.sipcm.sip.VoipAccountType;
import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.business.VoipVendorService;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;
import com.sipcm.web.util.JSFUtils;
import com.sipcm.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "voipAccountSettingBean")
@ViewScoped
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

	private UserSipProfile userSipProfile;

	private Map<Long, String> voipAccountPasswordMap;

	private List<UserVoipAccount> voipAccounts;

	private String sipProfilePhoneNumber = "";

	private String sipProfileDefaultArea = "";

	private boolean sipProfileAllowInternal = true;

	private UserVoipAccount selectedVoipAccount;

	@PostConstruct
	public void init() {
		User user = getCurrentUser();
		if (user == null) {
			throw new IllegalStateException(
					"Access this page without user? Can't be!");
		}
		userSipProfile = getUserSipProfile(user);
		if (userSipProfile != null) {
			sipProfilePhoneNumber = userSipProfile.getPhoneNumber();
			sipProfileDefaultArea = userSipProfile.getDefaultAreaCode();
			sipProfileAllowInternal = userSipProfile.isAllowLocalDirectly();
			Collection<UserVoipAccount> accounts = getUserVoipAccountService()
					.getUserVoipAccount(userSipProfile);
			voipAccountPasswordMap = new HashMap<Long, String>();
			voipAccounts = new ArrayList<UserVoipAccount>();
			if (accounts != null) {
				for (UserVoipAccount account : accounts) {
					voipAccountPasswordMap.put(account.getId(),
							account.getPassword());
					account.setPassword(null);
					voipAccounts.add(account);
				}
			}
		}
	}

	public void saveSipProfile() {
		try {
			if (userSipProfile == null) {
				userSipProfile = getUserSipProfileService()
						.createUserSipProfile(getCurrentUser());
			}
			Long id = userSipProfile.getId();
			userSipProfile.setPhoneNumber(sipProfilePhoneNumber);
			userSipProfile.setDefaultAreaCode(sipProfileDefaultArea);
			userSipProfile.setAllowLocalDirectly(sipProfileAllowInternal);
			getUserSipProfileService().saveEntity(userSipProfile);
			if (id == null) {
				voipAccounts = new ArrayList<UserVoipAccount>();
				voipAccountPasswordMap = new HashMap<Long, String>();
			}
			FacesMessage message = Messages.getMessage(
					"member.sipprofile.success", FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.getLocalizedMessage(), null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void accountRowEdit(RowEditEvent event) {
		try {
			UserVoipAccount account = (UserVoipAccount) event.getObject();
			if (validateUserVoipAccount(account)) {
				if (account.getPassword() == null
						&& voipAccountPasswordMap.get(account) != null) {
					account.setPassword(voipAccountPasswordMap.get(account));
				}
				getUserVoipAccountService().saveEntity(account);
				voipAccountPasswordMap.put(account.getId(),
						account.getPassword());
				account.setPassword(null);
				FacesMessage message = Messages.getMessage(
						"member.voip.accounts.save.success",
						FacesMessage.SEVERITY_INFO);
				FacesContext.getCurrentInstance().addMessage(null, message);
			}
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.getLocalizedMessage(), null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void saveAccount(ActionEvent actionEvent) {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean saved = false;
		try {
			if (validateUserVoipAccount(selectedVoipAccount)) {
				if (selectedVoipAccount.getPassword() == null
						&& selectedVoipAccount.getId() != null
						&& voipAccountPasswordMap.get(selectedVoipAccount
								.getId()) != null) {
					selectedVoipAccount.setPassword(voipAccountPasswordMap
							.get(selectedVoipAccount.getId()));
				}
				Long id = selectedVoipAccount.getId();
				getUserVoipAccountService().saveEntity(selectedVoipAccount);
				voipAccountPasswordMap.put(selectedVoipAccount.getId(),
						selectedVoipAccount.getPassword());
				selectedVoipAccount.setPassword(null);
				if (id == null) {
					voipAccounts.add(selectedVoipAccount);
				}
				FacesMessage message = Messages.getMessage(
						"member.voip.accounts.save.success",
						FacesMessage.SEVERITY_INFO);
				FacesContext.getCurrentInstance().addMessage(null, message);
				saved = true;
			}
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.getLocalizedMessage(), null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		context.addCallbackParam("saved", saved);
	}

	private boolean validateUserVoipAccount(UserVoipAccount account) {
		boolean ret = true;
		if (account.getPassword() == null
				&& voipAccountPasswordMap.get(account.getId()) == null) {
			FacesMessage message = Messages.getMessage(
					"member.voip.error.password.required",
					FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			ret = false;
		}
		if (VoipVendorType.GOOGLE_VOICE.equals(account.getVoipVendor()
				.getType())) {
			if (!VoipAccountType.OUTGOING.equals(account.getType())) {
				FacesMessage message = Messages.getMessage(
						"member.voip.error.googlevoice.outgoing.only",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				ret = false;
			}
			if (account.getPhoneNumber() == null) {
				FacesMessage message = Messages.getMessage(
						"member.voip.error.googlevoice.phonenumber.required",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				ret = false;
			}
			if (account.getCallBackNumber() == null) {
				FacesMessage message = Messages.getMessage(
						"member.voip.error.googlevoice.callback.required",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				ret = false;
			}
		} else {
			if (!VoipAccountType.OUTGOING.equals(account.getType())) {
				if (account.getPhoneNumber() == null) {
					FacesMessage message = Messages.getMessage(
							"member.voip.error.income.phonenumber.required",
							FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, message);
					ret = false;
				}
			}
			if (account.getCallBackNumber() != null) {
				FacesMessage message = Messages.getMessage(
						"member.voip.error.sip.no.callback",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				ret = false;
			}
		}
		return ret;
	}

	public void addVoipAccount() {
		try {
			UserVoipAccount account = getUserVoipAccountService()
					.createNewEntity();
			account.setOwnser(userSipProfile);
			selectedVoipAccount = account;
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.getLocalizedMessage(), null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void removeVoipAccount(ActionEvent action) {
		getUserVoipAccountService().removeEntity(selectedVoipAccount);
		voipAccountPasswordMap.remove(selectedVoipAccount.getId());
		voipAccounts.remove(selectedVoipAccount);
		FacesMessage message = Messages.getMessage(
				"member.voip.account.deleted", FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * @return the voipAccounts
	 */
	public List<UserVoipAccount> getVoipAccounts() {
		return voipAccounts;
	}

	public Collection<VoipVendor> getVoipVendors() {
		return getVoipVendorService().getManagableVoipVendors();
	}

	public VoipAccountType[] getVoipAccountTypes() {
		return VoipAccountType.values();
	}

	private UserSipProfile getUserSipProfile(User user) {
		userSipProfile = getUserSipProfileService().getUserSipProfileByUser(
				user);
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
			user = getUserService().getUserByUsername(username);
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

	private VoipVendorService getVoipVendorService() {
		if (voipVendorService == null) {
			voipVendorService = JSFUtils.getManagedBean("voipVendorService",
					VoipVendorService.class);
		}
		return voipVendorService;
	}

	/**
	 * @param userSipProfileService
	 *            the userSipProfileService to set
	 */
	public void setUserSipProfileService(
			UserSipProfileService userSipProfileService) {
		this.userSipProfileService = userSipProfileService;
	}

	private UserSipProfileService getUserSipProfileService() {
		if (userSipProfileService == null) {
			userSipProfileService = JSFUtils.getManagedBean(
					"userSipProfileService", UserSipProfileService.class);
		}
		return userSipProfileService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private UserService getUserService() {
		if (userService == null) {
			userService = JSFUtils.getManagedBean("userService",
					UserService.class);
		}
		return userService;
	}

	/**
	 * @param userVoipAccountService
	 *            the userVoipAccountService to set
	 */
	public void setUserVoipAccountService(
			UserVoipAccountService userVoipAccountService) {
		this.userVoipAccountService = userVoipAccountService;
	}

	private UserVoipAccountService getUserVoipAccountService() {
		if (userVoipAccountService == null) {
			userVoipAccountService = JSFUtils.getManagedBean(
					"userVoipAccountService", UserVoipAccountService.class);
		}
		return userVoipAccountService;
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

	/**
	 * @param selectedVoipAccount
	 *            the selectedVoipAccount to set
	 */
	public void setSelectedVoipAccount(UserVoipAccount selectedVoipAccount) {
		this.selectedVoipAccount = selectedVoipAccount;
	}

	/**
	 * @return the selectedVoipAccount
	 */
	public UserVoipAccount getSelectedVoipAccount() {
		return selectedVoipAccount;
	}
}
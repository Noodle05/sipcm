/**
 * 
 */
package com.mycallstation.web.member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mycallstation.constant.PhoneNumberStatus;
import com.mycallstation.constant.VoipAccountType;
import com.mycallstation.constant.VoipVendorType;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.business.UserVoipAccountService;
import com.mycallstation.dataaccess.business.VoipVendorService;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.dataaccess.model.VoipVendor;
import com.mycallstation.googlevoice.GoogleVoiceManager;
import com.mycallstation.googlevoice.GoogleVoiceSession;
import com.mycallstation.googlevoice.setting.GoogleVoiceConfig;
import com.mycallstation.googlevoice.setting.Phone;
import com.mycallstation.googlevoice.setting.PhoneType;
import com.mycallstation.scope.ViewScope;
import com.mycallstation.web.util.JSFUtils;
import com.mycallstation.web.util.Messages;

/**
 * @author Wei Gao
 * 
 */
@Component("voipAccountSettingBean")
@Scope(ViewScope.VIEW_SCOPE)
public class VoipAccountSettingBean implements Serializable {
	private static final long serialVersionUID = -4107844991260539763L;

	private static final Logger logger = LoggerFactory
			.getLogger(VoipAccountSettingBean.class);

	private static final int INVALID = 0;
	private static final int VALID = 1;
	private static final int VERIFIED = 2;

	@Resource(name = "userVoipAccountService")
	private UserVoipAccountService userVoipAccountService;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "googleVoiceManager")
	private GoogleVoiceManager googleVoiceManager;

	@Resource(name = "voipVendorService")
	private VoipVendorService voipVendorService;

	@Resource(name = "jsfUtils")
	private JSFUtils jsfUtils;

	@Resource(name = "web.messages")
	private Messages messages;

	private UserSipProfile userSipProfile;

	private Map<Long, String> voipAccountPasswordMap;

	private List<UserVoipAccount> voipAccounts;

	private String sipProfilePhoneNumber;

	private String sipProfileDefaultArea;

	private boolean sipProfileAllowInternal = true;

	private UserVoipAccount selectedVoipAccount;

	@PostConstruct
	public void init() {
		User user = jsfUtils.getCurrentUser();
		if (user == null) {
			throw new IllegalStateException(
					"Access this page without user? Can't be!");
		}
		voipAccountPasswordMap = new HashMap<Long, String>();
		voipAccounts = new ArrayList<UserVoipAccount>();
		userSipProfile = getUserSipProfile(user);
		if (userSipProfile != null) {
			sipProfilePhoneNumber = userSipProfile.getPhoneNumber();
			sipProfileDefaultArea = userSipProfile.getDefaultAreaCode();
			sipProfileAllowInternal = userSipProfile.isAllowLocalDirectly();
			Collection<UserVoipAccount> accounts = userVoipAccountService
					.getUserVoipAccount(userSipProfile);
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
				userSipProfile = userSipProfileService
						.createUserSipProfile(jsfUtils.getCurrentUser());
			}
			userSipProfile.setPhoneNumber(sipProfilePhoneNumber);
			userSipProfile.setDefaultAreaCode(sipProfileDefaultArea);
			userSipProfile.setAllowLocalDirectly(sipProfileAllowInternal);
			userSipProfileService.saveEntity(userSipProfile);
			FacesMessage message = messages.getMessage(
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
			if (validateUserVoipAccount(account) != INVALID) {
				if (account.getPassword() == null
						&& voipAccountPasswordMap.get(account) != null) {
					account.setPassword(voipAccountPasswordMap.get(account));
				}
				userVoipAccountService.saveEntity(account);
				voipAccountPasswordMap.put(account.getId(),
						account.getPassword());
				account.setPassword(null);
				FacesMessage message = messages.getMessage(
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
			if (selectedVoipAccount.getPassword() == null
					&& selectedVoipAccount.getId() != null
					&& voipAccountPasswordMap.get(selectedVoipAccount.getId()) != null) {
				selectedVoipAccount.setPassword(voipAccountPasswordMap
						.get(selectedVoipAccount.getId()));
			}
			int v = validateUserVoipAccount(selectedVoipAccount);
			if (v != INVALID) {
				Long id = selectedVoipAccount.getId();
				userVoipAccountService.saveEntity(selectedVoipAccount);
				voipAccountPasswordMap.put(selectedVoipAccount.getId(),
						selectedVoipAccount.getPassword());
				selectedVoipAccount.setPassword(null);
				if (id == null) {
					voipAccounts.add(selectedVoipAccount);
				}
				if (v == VERIFIED) {
					userSipProfile
							.setPhoneNumberStatus(PhoneNumberStatus.GOOGLEVOICEVERIFIED);
					userSipProfileService.saveEntity(userSipProfile);
				}
				FacesMessage message = messages.getMessage(
						"member.voip.accounts.save.success",
						FacesMessage.SEVERITY_INFO);
				FacesContext.getCurrentInstance().addMessage(null, message);
				saved = true;
			}
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error happened when save account.", e);
			}
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.getLocalizedMessage(), null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		context.addCallbackParam("saved", saved);
	}

	public void validatePhoneNumber(FacesContext context,
			UIComponent componentToValidate, Object value) {
		String phoneNumber = (String) value;
		User user = jsfUtils.getCurrentUser();
		UserSipProfile userSipProfile = userSipProfileService
				.getUserSipProfileByVerifiedPhoneNumber(phoneNumber);
		if (userSipProfile != null && !userSipProfile.getOwner().equals(user)) {
			FacesMessage message = messages.getMessage(
					"member.sipprofile.error.phoneNumber.exists",
					FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}

	private int validateUserVoipAccount(UserVoipAccount account) {
		int ret = VALID;
		if (account.getPassword() == null
				&& voipAccountPasswordMap.get(account.getId()) == null) {
			FacesMessage message = messages.getMessage(
					"member.voip.error.password.required",
					FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			ret = INVALID;
		}
		if (VoipVendorType.GOOGLE_VOICE.equals(account.getVoipVendor()
				.getType())) {
			if (!VoipAccountType.OUTGOING.equals(account.getType())) {
				FacesMessage message = messages.getMessage(
						"member.voip.error.googlevoice.outgoing.only",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				ret = INVALID;
			}
			if (account.getPhoneNumber() == null) {
				FacesMessage message = messages.getMessage(
						"member.voip.error.googlevoice.phonenumber.required",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				ret = INVALID;
			}
			if (account.getCallBackNumber() == null) {
				FacesMessage message = messages.getMessage(
						"member.voip.error.googlevoice.callback.required",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				ret = INVALID;
			}
			if (ret == VALID && validateGoogleVoiceAccount(account)) {
				ret = VERIFIED;
			}
		} else {
			if (!VoipAccountType.OUTGOING.equals(account.getType())) {
				if (account.getPhoneNumber() == null) {
					FacesMessage message = messages.getMessage(
							"member.voip.error.income.phonenumber.required",
							FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, message);
					ret = INVALID;
				}
			}
			if (account.getCallBackNumber() != null) {
				FacesMessage message = messages.getMessage(
						"member.voip.error.sip.no.callback",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				ret = INVALID;
			}
		}
		return ret;
	}

	private boolean validateGoogleVoiceAccount(UserVoipAccount account) {
		boolean ret = true;
		GoogleVoiceSession session = googleVoiceManager.getGoogleVoiceSession(
				account.getAccount(), account.getPassword());
		try {
			try {
				session.login();
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Error happened when login.", e);
				}
				FacesMessage message = messages.getMessage(
						"member.voip.warn.googlevoice.cannot.login",
						FacesMessage.SEVERITY_WARN);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return false;
			}
			try {
				GoogleVoiceConfig conf = session.getGoogleVoiceSetting();
				if (!account.getPhoneNumber().equals(
						conf.getSettings().getPrimaryDid())) {
					FacesMessage message = messages.getMessage(
							"member.voip.warn.googlevoice.number.not.match",
							FacesMessage.SEVERITY_WARN);
					FacesContext.getCurrentInstance().addMessage(null, message);
					ret = false;
				}
				Map<Integer, Phone> phones = conf.getPhones();
				if (phones == null) {
					FacesMessage message = messages.getMessage(
							"member.voip.warn.googlevoice.no.callback.number",
							FacesMessage.SEVERITY_WARN);
					FacesContext.getCurrentInstance().addMessage(null, message);
					return false;
				}
				Phone ph = null;
				for (Entry<Integer, Phone> entry : phones.entrySet()) {
					Phone p = entry.getValue();
					if (p != null
							&& account.getCallBackNumber().equals(
									p.getPhoneNumber())) {
						ph = p;
						break;
					}
				}
				if (ph == null) {
					FacesMessage message = messages.getMessage(
							"member.voip.warn.googlevoice.callback.not.match",
							FacesMessage.SEVERITY_WARN);
					FacesContext.getCurrentInstance().addMessage(null, message);
					ret = false;
				} else {
					if (!PhoneType.HOME.equals(ph.getType())) {
						FacesMessage message = messages
								.getMessage(
										"member.voip.warn.googlevoice.callback.type.error",
										FacesMessage.SEVERITY_WARN);
						FacesContext.getCurrentInstance().addMessage(null,
								message);
						ret = false;
					}
					Map<Integer, Boolean> disids = conf.getSettings()
							.getDisabledIdMap();
					Boolean disabled = disids.get(ph.getId());
					if (disabled != null && disabled) {
						FacesMessage message = messages
								.getMessage(
										"member.voip.warn.googlevoice.callback.disabled",
										FacesMessage.SEVERITY_WARN);
						FacesContext.getCurrentInstance().addMessage(null,
								message);
						ret = false;
					}
				}
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Error happened when getting google voice setting",
							e);
					FacesMessage message = messages.getMessage(
							"member.voip.warn.googlevoice.cannot.get.setting",
							FacesMessage.SEVERITY_WARN);
					FacesContext.getCurrentInstance().addMessage(null, message);
					ret = false;
				}
			}
		} finally {
			session.logout();
		}
		return ret;
	}

	public void addVoipAccount() {
		try {
			UserVoipAccount account = userVoipAccountService.createNewEntity();
			account.setOwner(userSipProfile);
			selectedVoipAccount = account;
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.getLocalizedMessage(), null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void removeVoipAccount(ActionEvent action) {
		userVoipAccountService.removeEntity(selectedVoipAccount);
		voipAccountPasswordMap.remove(selectedVoipAccount.getId());
		voipAccounts.remove(selectedVoipAccount);
		FacesMessage message = messages.getMessage(
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
		return voipVendorService.getManagableVoipVendors();
	}

	public VoipAccountType[] getVoipAccountTypes() {
		return VoipAccountType.values();
	}

	private UserSipProfile getUserSipProfile(User user) {
		userSipProfile = userSipProfileService.getUserSipProfileByUser(user);
		return userSipProfile;
	}

	public boolean isHasSipProfile() {
		return userSipProfile != null;
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

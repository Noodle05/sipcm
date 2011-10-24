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

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.event.FlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.constant.PhoneNumberStatus;
import com.mycallstation.constant.VoipAccountType;
import com.mycallstation.constant.VoipVendorType;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.business.UserVoipAccountService;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.dataaccess.model.VoipVendor;
import com.mycallstation.googlevoice.GoogleVoiceSession;
import com.mycallstation.googlevoice.setting.GoogleVoiceConfig;
import com.mycallstation.googlevoice.setting.Phone;
import com.mycallstation.googlevoice.setting.PhoneType;
import com.mycallstation.web.util.JSFUtils;
import com.mycallstation.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "voipSettingWizardBean")
@ViewScoped
public class VoipSettingWizardBean implements Serializable {
	private static final long serialVersionUID = 3292589636636863869L;

	private static final Logger logger = LoggerFactory
			.getLogger(VoipSettingWizardBean.class);

	private String oriSipNumber;

	private String sipNumber;

	private String sipAreaCode;

	private boolean sipAllowInternal = true;

	private boolean sipNumberVerified = false;

	private Long gvId;

	private String gvName = "google voice";

	private String oriGvAccount;

	private String gvAccount;

	private String oriGvPass;

	private String gvPass;

	private String oriGvNumber;

	private String gvNumber;

	private String gvCallback;

	private PhoneType gvCallbackType;

	private GoogleVoiceConfig gvConfig;

	private boolean hasIncome = true;

	private Long inId;

	private VoipVendor inVendor;

	private String inName = "Incoming";

	private String inAccount;

	private String oriInPass;

	private String inPass;

	private String inNumber;

	private boolean inOut;

	private boolean hasOutgo = true;

	private Long outId;

	private VoipVendor outVendor;

	private String outName = "Outgoing";

	private String outAccount;

	private String oriOutPass;

	private String outPass;

	private String outNumber;

	private boolean saveDisabled = true;

	private Map<String, PhoneType> callbackList = new HashMap<String, PhoneType>();

	@PostConstruct
	public void init() {
		User user = JSFUtils.getCurrentUser();
		if (user == null) {
			throw new IllegalStateException(
					"Access this page without user? Can't be!");
		}
		UserSipProfile userSipProfile = JSFUtils.getUserSipProfileService()
				.getUserSipProfileByUser(user);
		if (userSipProfile != null) {
			oriSipNumber = sipNumber = userSipProfile.getPhoneNumber();
			sipAreaCode = userSipProfile.getDefaultAreaCode();
			sipAllowInternal = userSipProfile.isAllowLocalDirectly();
			sipNumberVerified = userSipProfile.getPhoneNumberStatus()
					.isVerified();
			Collection<UserVoipAccount> accounts = JSFUtils
					.getUserVoipAccountService().getUserVoipAccount(
							userSipProfile);
			hasIncome = false;
			hasOutgo = false;
			if (accounts != null) {
				for (UserVoipAccount account : accounts) {
					if (VoipVendorType.GOOGLE_VOICE.equals(account
							.getVoipVendor().getType()) && gvId == null) {
						gvId = account.getId();
						gvName = account.getName();
						oriGvAccount = gvAccount = account.getAccount();
						oriGvPass = account.getPassword();
						oriGvNumber = gvNumber = account.getPhoneNumber();
						gvCallback = account.getCallBackNumber();
						gvCallbackType = account.getCallBackType() == null ? PhoneType.HOME
								: PhoneType.valueOf(account.getCallBackType());
					} else if (!VoipAccountType.OUTGOING.equals(account
							.getType()) && inId == null) {
						hasIncome = true;
						inId = account.getId();
						inVendor = account.getVoipVendor();
						inName = account.getName();
						inAccount = account.getAccount();
						oriInPass = account.getPassword();
						inNumber = account.getPhoneNumber();
						if (VoipAccountType.BOTH.equals(account.getType())) {
							inOut = true;
						}
					} else if (VoipAccountType.OUTGOING.equals(account
							.getType()) && outId == null) {
						hasOutgo = true;
						outId = account.getId();
						outVendor = account.getVoipVendor();
						outName = account.getName();
						outAccount = account.getAccount();
						oriOutPass = account.getPassword();
						outNumber = account.getPhoneNumber();
					}
				}
			}
		}
	}

	public String onFlowProcess(FlowEvent event) {
		String cs = event.getOldStep();
		String ns = event.getNewStep();
		logger.debug("Current wizard step:" + event.getOldStep());
		logger.debug("Next step:" + event.getNewStep());

		if ("gv-initial".equals(cs)) {
			if (gvPass == null && oriGvPass == null) {
				FacesMessage message = Messages.getMessage(
						"member.voip.wizard.error.gv.password.required",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return "gv-initial";
			}
			if (!gvAccount.equals(oriGvAccount) || (gvPass != null)) {
				validateGV();
			}
		}
		if ("incoming-setting".equals(cs) && "outgoing-setting".equals(ns)) {
			if (inPass == null && oriInPass == null) {
				FacesMessage message = Messages.getMessage(
						"member.voip.wizard.error.in.password.required",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return "incoming-setting";
			}
		}
		if ("outgoing-setting".equals(cs) && "confirm".equals(ns)) {
			if (outPass == null && oriOutPass == null) {
				FacesMessage message = Messages.getMessage(
						"member.voip.wizard.error.out.password.required",
						FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return "outgoing-setting";
			}
		}
		String target = ns;
		if ("incoming-setting".equals(ns)) {
			if (!hasIncome) {
				if ("gv-setting".equals(cs)) {
					if (!hasOutgo) {
						target = "confirm";
					} else {
						target = "outgoing-setting";
					}
				} else {
					target = "gv-setting";
				}
			}
		} else if ("outgoing-setting".equals(ns)) {
			if (!hasOutgo) {
				if ("confirm".equals(cs)) {
					if (!hasIncome) {
						target = "gv-setting";
					} else {
						target = "incoming-setting";
					}
				} else {
					target = "confirm";
				}
			}
		}
		if ("confirm".equals(target)) {
			validatePhoneNumber();
		}
		return target;
	}

	private void validateGV() {
		gvConfig = null;
		String pass = gvPass == null ? oriGvPass : gvPass;
		GoogleVoiceSession gvs = JSFUtils.getGoogleVoiceManager()
				.getGoogleVoiceSession(gvAccount, pass);
		try {
			try {
				gvs.login();
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Cannot login google voice.", e);
				}
				FacesMessage message = Messages.getMessage(
						"member.voip.warn.googlevoice.cannot.login",
						FacesMessage.SEVERITY_WARN);
				FacesContext.getCurrentInstance().addMessage(null, message);
			}
			oriGvAccount = gvAccount;
			oriGvPass = gvPass;
			if (gvs.isLoggedIn()) {
				try {
					gvConfig = gvs.getGoogleVoiceSetting();
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Cannot get google voice config.", e);
					}
					FacesMessage message = Messages.getMessage(
							"member.voip.warn.googlevoice.cannot.get.setting",
							FacesMessage.SEVERITY_WARN);
					FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
			if (gvConfig != null) {
				sipNumber = gvConfig.getSettings().getPrimaryDid();
				sipAreaCode = sipNumber.substring(2, 5);
				gvNumber = gvConfig.getSettings().getPrimaryDid();
				if (gvConfig.getPhones() != null) {
					for (Phone phone : gvConfig.getPhones().values()) {
						Boolean disabled = gvConfig.getSettings()
								.getDisabledIdMap() == null ? null : gvConfig
								.getSettings().getDisabledIdMap()
								.get(phone.getId());
						if (disabled == null || !disabled) {
							callbackList.put(phone.getPhoneNumber(),
									phone.getType());
							if (gvCallback == null || gvCallbackType == null
									|| gvCallbackType.lessThan(phone.getType())) {
								gvCallback = phone.getPhoneNumber();
								gvCallbackType = phone.getType();
							}
						}
					}
				}
			}
		} finally {
			gvs.logout();
		}
	}

	private void validatePhoneNumber() {
		UserSipProfile userSipProfile = JSFUtils.getUserSipProfileService()
				.getUserSipProfileByVerifiedPhoneNumber(sipNumber);
		if (gvName.equalsIgnoreCase(inName) || inName.equalsIgnoreCase(outName)
				|| outName.equalsIgnoreCase(gvName)) {
			FacesMessage message = Messages.getMessage(
					"member.voip.wizard.error.account.name.conflict",
					FacesMessage.SEVERITY_WARN);
			FacesContext.getCurrentInstance().addMessage(null, message);
			saveDisabled = true;
		} else if (userSipProfile != null
				&& !userSipProfile.getOwner().equals(JSFUtils.getCurrentUser())) {
			FacesMessage message = Messages.getMessage(
					"member.voip.wizard.error.phone.been.used",
					FacesMessage.SEVERITY_WARN);
			FacesContext.getCurrentInstance().addMessage(null, message);
			saveDisabled = true;
		} else {
			saveDisabled = false;
		}
	}

	public String save() {
		User user = JSFUtils.getCurrentUser();
		if (user == null) {
			throw new IllegalStateException(
					"Access this page without user? Can't be!");
		}
		UserSipProfileService userSipProfileService = JSFUtils
				.getUserSipProfileService();
		UserSipProfile userSipProfile = userSipProfileService
				.getUserSipProfileByUser(user);
		if (userSipProfile == null) {
			userSipProfile = userSipProfileService.createUserSipProfile(user);
		}
		userSipProfile.setPhoneNumber(sipNumber);
		userSipProfile.setAllowLocalDirectly(sipAllowInternal);
		userSipProfile
				.setPhoneNumberStatus(isGvVerified() ? PhoneNumberStatus.GOOGLEVOICEVERIFIED
						: PhoneNumberStatus.UNVERIFIED);
		userSipProfile.setDefaultAreaCode(sipAreaCode);

		Collection<UserVoipAccount> accounts = new ArrayList<UserVoipAccount>(3);
		Collection<UserVoipAccount> removeAccs = new ArrayList<UserVoipAccount>();

		UserVoipAccountService userVoipAccountService = JSFUtils
				.getUserVoipAccountService();
		UserVoipAccount gvAccount = null;
		if (gvId != null) {
			gvAccount = userVoipAccountService.getEntityById(gvId);
		}
		if (gvAccount == null) {
			gvAccount = userVoipAccountService.createNewEntity();
			gvAccount.setOwner(userSipProfile);
			gvAccount.setVoipVendor(JSFUtils.getVoipVendorService()
					.getGoogleVoiceVendor());
			gvAccount.setType(VoipAccountType.OUTGOING);
		}
		gvAccount.setName(gvName);
		gvAccount.setAccount(this.gvAccount);
		if (gvPass != null) {
			gvAccount.setPassword(gvPass);
		}
		gvAccount.setPhoneNumber(gvNumber);
		gvAccount.setCallBackNumber(gvCallback);
		gvAccount.setCallBackType(gvCallbackType.getValue());
		accounts.add(gvAccount);

		UserVoipAccount inAcc = null;
		if (inId != null) {
			inAcc = userVoipAccountService.getEntityById(inId);
		}
		if (hasIncome) {
			if (inAcc == null) {
				inAcc = userVoipAccountService.createNewEntity();
				inAcc.setOwner(userSipProfile);
			}
			inAcc.setName(inName);
			inAcc.setVoipVendor(inVendor);
			inAcc.setAccount(inAccount);
			if (inPass != null) {
				inAcc.setPassword(inPass);
			}
			inAcc.setPhoneNumber(inNumber);
			if (inOut) {
				inAcc.setType(VoipAccountType.BOTH);
			} else {
				inAcc.setType(VoipAccountType.INCOME);
			}
			accounts.add(inAcc);
		} else {
			if (inAcc != null) {
				removeAccs.add(inAcc);
			}
		}

		UserVoipAccount outAcc = null;
		if (outId != null) {
			outAcc = userVoipAccountService.getEntityById(outId);
		}
		if (hasOutgo) {
			if (outAcc == null) {
				outAcc = userVoipAccountService.createNewEntity();
				outAcc.setOwner(userSipProfile);
			}
			outAcc.setName(outName);
			outAcc.setVoipVendor(outVendor);
			outAcc.setAccount(outAccount);
			if (outPass != null) {
				outAcc.setPassword(outPass);
			}
			outAcc.setPhoneNumber(outNumber);
			outAcc.setType(VoipAccountType.OUTGOING);
			accounts.add(outAcc);
		} else {
			if (outAcc != null) {
				removeAccs.add(outAcc);
			}
		}

		userSipProfileService.saveEntity(userSipProfile);
		if (!removeAccs.isEmpty()) {
			userVoipAccountService.removeEntities(removeAccs);
		}
		if (!accounts.isEmpty()) {
			userVoipAccountService.saveEntities(accounts);
		}

		return "/member/index.jsf?faces-redirect=true";
	}

	public SelectItem[] getGvCallbackTypes() {
		return JSFUtils.getAvailableGvPhoneType();
	}

	public List<String> completeCallback(String query) {
		List<String> result = new ArrayList<String>();
		for (String c : callbackList.keySet()) {
			if (c.startsWith(query)) {
				result.add(c);
			}
		}
		return result;
	}

	public Collection<VoipVendor> getSipVendors() {
		return JSFUtils.getVoipVendorService().getSIPVendors();
	}

	/**
	 * @param sipNumber
	 *            the sipNumber to set
	 */
	public void setSipNumber(String sipNumber) {
		this.sipNumber = sipNumber;
	}

	/**
	 * @return the sipNumber
	 */
	public String getSipNumber() {
		return sipNumber;
	}

	/**
	 * @param sipAreaCode
	 *            the sipAreaCode to set
	 */
	public void setSipAreaCode(String sipAreaCode) {
		this.sipAreaCode = sipAreaCode;
	}

	/**
	 * @return the sipAreaCode
	 */
	public String getSipAreaCode() {
		return sipAreaCode;
	}

	/**
	 * @param sipAllowInternal
	 *            the sipAllowInternal to set
	 */
	public void setSipAllowInternal(boolean sipAllowInternal) {
		this.sipAllowInternal = sipAllowInternal;
	}

	/**
	 * @return the sipAllowInternal
	 */
	public boolean isSipAllowInternal() {
		return sipAllowInternal;
	}

	/**
	 * @param sipNumberVerified
	 *            the sipNumberVerified to set
	 */
	public void setSipNumberVerified(boolean sipNumberVerified) {
		this.sipNumberVerified = sipNumberVerified;
	}

	/**
	 * @return the sipNumberVerified
	 */
	public boolean isSipNumberVerified() {
		return sipNumberVerified;
	}

	/**
	 * @param gvName
	 *            the gvName to set
	 */
	public void setGvName(String gvName) {
		this.gvName = gvName;
	}

	/**
	 * @return the gvName
	 */
	public String getGvName() {
		return gvName;
	}

	/**
	 * @param gvAccount
	 *            the gvAccount to set
	 */
	public void setGvAccount(String gvAccount) {
		this.gvAccount = gvAccount;
	}

	/**
	 * @return the gvAccount
	 */
	public String getGvAccount() {
		return gvAccount;
	}

	/**
	 * @param gvPass
	 *            the gvPass to set
	 */
	public void setGvPass(String gvPass) {
		this.gvPass = gvPass;
	}

	/**
	 * @return the gvPass
	 */
	public String getGvPass() {
		return gvPass;
	}

	/**
	 * @param gvNumber
	 *            the gvNumber to set
	 */
	public void setGvNumber(String gvNumber) {
		this.gvNumber = gvNumber;
	}

	/**
	 * @return the gvNumber
	 */
	public String getGvNumber() {
		return gvNumber;
	}

	/**
	 * @param gvCallback
	 *            the gvCallback to set
	 */
	public void setGvCallback(String gvCallback) {
		this.gvCallback = gvCallback;
	}

	/**
	 * @return the gvCallback
	 */
	public String getGvCallback() {
		return gvCallback;
	}

	/**
	 * @param gvCallbackType
	 *            the gvCallbackType to set
	 */
	public void setGvCallbackType(PhoneType gvCallbackType) {
		this.gvCallbackType = gvCallbackType;
	}

	/**
	 * @return the gvCallbackType
	 */
	public PhoneType getGvCallbackType() {
		return gvCallbackType;
	}

	/**
	 * @param hasIncome
	 *            the hasIncome to set
	 */
	public void setHasIncome(boolean hasIncome) {
		this.hasIncome = hasIncome;
	}

	/**
	 * @return the hasIncome
	 */
	public boolean isHasIncome() {
		return hasIncome;
	}

	/**
	 * @param inVendor
	 *            the inVendor to set
	 */
	public void setInVendor(VoipVendor inVendor) {
		this.inVendor = inVendor;
	}

	/**
	 * @return the inVendor
	 */
	public VoipVendor getInVendor() {
		return inVendor;
	}

	/**
	 * @param inName
	 *            the inName to set
	 */
	public void setInName(String inName) {
		this.inName = inName;
	}

	/**
	 * @return the inName
	 */
	public String getInName() {
		return inName;
	}

	/**
	 * @param inAccount
	 *            the inAccount to set
	 */
	public void setInAccount(String inAccount) {
		this.inAccount = inAccount;
	}

	/**
	 * @return the inAccount
	 */
	public String getInAccount() {
		return inAccount;
	}

	/**
	 * @param inPass
	 *            the inPass to set
	 */
	public void setInPass(String inPass) {
		this.inPass = inPass;
	}

	/**
	 * @return the inPass
	 */
	public String getInPass() {
		return inPass;
	}

	/**
	 * @param inNumber
	 *            the inNumber to set
	 */
	public void setInNumber(String inNumber) {
		this.inNumber = inNumber;
	}

	/**
	 * @return the inNumber
	 */
	public String getInNumber() {
		return inNumber;
	}

	/**
	 * @param inOut
	 *            the inOut to set
	 */
	public void setInOut(boolean inOut) {
		this.inOut = inOut;
	}

	/**
	 * @return the inOut
	 */
	public boolean isInOut() {
		return inOut;
	}

	/**
	 * @param hasOutgo
	 *            the hasOutgo to set
	 */
	public void setHasOutgo(boolean hasOutgo) {
		this.hasOutgo = hasOutgo;
	}

	/**
	 * @return the hasOutgo
	 */
	public boolean isHasOutgo() {
		return hasOutgo;
	}

	/**
	 * @param outVendor
	 *            the outVendor to set
	 */
	public void setOutVendor(VoipVendor outVendor) {
		this.outVendor = outVendor;
	}

	/**
	 * @return the outVendor
	 */
	public VoipVendor getOutVendor() {
		return outVendor;
	}

	/**
	 * @param outName
	 *            the outName to set
	 */
	public void setOutName(String outName) {
		this.outName = outName;
	}

	/**
	 * @return the outName
	 */
	public String getOutName() {
		return outName;
	}

	/**
	 * @param outAccount
	 *            the outAccount to set
	 */
	public void setOutAccount(String outAccount) {
		this.outAccount = outAccount;
	}

	/**
	 * @return the outAccount
	 */
	public String getOutAccount() {
		return outAccount;
	}

	/**
	 * @param outPass
	 *            the outPass to set
	 */
	public void setOutPass(String outPass) {
		this.outPass = outPass;
	}

	/**
	 * @return the outPass
	 */
	public String getOutPass() {
		return outPass;
	}

	/**
	 * @param outNumber
	 *            the outNumber to set
	 */
	public void setOutNumber(String outNumber) {
		this.outNumber = outNumber;
	}

	/**
	 * @return the outNumber
	 */
	public String getOutNumber() {
		return outNumber;
	}

	public boolean isGvVerified() {
		if (sipNumber.equals(oriSipNumber) && gvAccount.equals(oriGvAccount)
				&& gvNumber.equals(oriGvNumber)
				&& (gvPass == null || gvPass.equals(oriGvPass))
				&& sipNumberVerified) {
			return true;
		}
		return (gvConfig != null && gvConfig.getSettings().getPrimaryDid()
				.equals(sipNumber));
	}

	/**
	 * @return the saveDisabled
	 */
	public boolean isSaveDisabled() {
		return saveDisabled;
	}
}

/**
 * 
 */
package com.mycallstation.googlevoice.setting;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Wei Gao
 * 
 */
public class Settings implements Serializable {
	private static final long serialVersionUID = -684999314980950874L;
	// This is google voice number.
	private String primaryDid;
	private String language;
	// If directConnect = 0, this means Ask unknown callers name (1) or not (0)
	private int screenBehavior;
	// should set to false
	private boolean useDidAsCallerId;
	private int filterGlobalSpam;
	private int enablePinAccess;
	private int credits;
	private String timezone;
	private boolean doNotDisturb;
	// Unix time stamp of expire date.
	private long doNotDisturbExpiration = -1L;
	private transient Object[] didInfos;
	private transient Notification[] smsNotifications;
	private boolean emailNotificationActive;
	private String emailNotificationAddress;
	private boolean smsToEmailActive;
	private boolean smsToEmailSubject;
	private boolean missedToEmail;
	private boolean showTranscripts;
	// Direct connect or present screen behavior
	// For use with mycallstation, should set to true
	private boolean directConnect;
	// Enable recording (4), switch (*) and conferencing options on inbound
	// calls
	private int directRtp;
	// should set to false
	private boolean useDidAsSource;
	private boolean emailToSmsActive;
	private boolean missedToInbox;
	private Greeting[] greetings;
	private Map<Integer, Greeting> greetingsMap;
	private int[] activeForwardingIds;
	// If an phone is not forwarding, it will in this map with value: true
	private Map<Integer, Boolean> disabledIdMap;
	private int defaultGreetingId;
	private transient Object[] webCallButtons;
	private Map<String, Group> groups;
	private String[] groupList;
	private boolean lowBalanceNotificationEnabled;
	private String[] emailAddresses;
	private String baseUrl;

	/**
	 * @return the primaryDid
	 */
	public String getPrimaryDid() {
		return primaryDid;
	}

	/**
	 * @param primaryDid
	 *            the primaryDid to set
	 */
	public void setPrimaryDid(String primaryDid) {
		this.primaryDid = primaryDid;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the screenBehavior
	 */
	public int getScreenBehavior() {
		return screenBehavior;
	}

	/**
	 * @param screenBehavior
	 *            the screenBehavior to set
	 */
	public void setScreenBehavior(int screenBehavior) {
		this.screenBehavior = screenBehavior;
	}

	/**
	 * @return the useDidAsCallerId
	 */
	public boolean isUseDidAsCallerId() {
		return useDidAsCallerId;
	}

	/**
	 * @param useDidAsCallerId
	 *            the useDidAsCallerId to set
	 */
	public void setUseDidAsCallerId(boolean useDidAsCallerId) {
		this.useDidAsCallerId = useDidAsCallerId;
	}

	/**
	 * @return the filterGlobalSpam
	 */
	public int getFilterGlobalSpam() {
		return filterGlobalSpam;
	}

	/**
	 * @param filterGlobalSpam
	 *            the filterGlobalSpam to set
	 */
	public void setFilterGlobalSpam(int filterGlobalSpam) {
		this.filterGlobalSpam = filterGlobalSpam;
	}

	/**
	 * @return the enablePinAccess
	 */
	public int getEnablePinAccess() {
		return enablePinAccess;
	}

	/**
	 * @param enablePinAccess
	 *            the enablePinAccess to set
	 */
	public void setEnablePinAccess(int enablePinAccess) {
		this.enablePinAccess = enablePinAccess;
	}

	/**
	 * @return the credits
	 */
	public int getCredits() {
		return credits;
	}

	/**
	 * @param credits
	 *            the credits to set
	 */
	public void setCredits(int credits) {
		this.credits = credits;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone
	 *            the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the doNotDisturb
	 */
	public boolean isDoNotDisturb() {
		return doNotDisturb;
	}

	/**
	 * @param doNotDisturb
	 *            the doNotDisturb to set
	 */
	public void setDoNotDisturb(boolean doNotDisturb) {
		this.doNotDisturb = doNotDisturb;
	}

	/**
	 * @return the doNotDisturbExpiration
	 */
	public long getDoNotDisturbExpiration() {
		return doNotDisturbExpiration;
	}

	/**
	 * @param doNotDisturbExpiration
	 *            the doNotDisturbExpiration to set
	 */
	public void setDoNotDisturbExpiration(long doNotDisturbExpiration) {
		this.doNotDisturbExpiration = doNotDisturbExpiration;
	}

	/**
	 * @return the didInfos
	 */
	public Object[] getDidInfos() {
		return didInfos;
	}

	/**
	 * @param didInfos
	 *            the didInfos to set
	 */
	public void setDidInfos(Object[] didInfos) {
		this.didInfos = didInfos;
	}

	/**
	 * @return the smsNotifications
	 */
	public Notification[] getSmsNotifications() {
		return smsNotifications;
	}

	/**
	 * @param smsNotifications
	 *            the smsNotifications to set
	 */
	public void setSmsNotifications(Notification[] smsNotifications) {
		this.smsNotifications = smsNotifications;
	}

	/**
	 * @return the emailNotificationActive
	 */
	public boolean isEmailNotificationActive() {
		return emailNotificationActive;
	}

	/**
	 * @param emailNotificationActive
	 *            the emailNotificationActive to set
	 */
	public void setEmailNotificationActive(boolean emailNotificationActive) {
		this.emailNotificationActive = emailNotificationActive;
	}

	/**
	 * @return the emailNotificationAddress
	 */
	public String getEmailNotificationAddress() {
		return emailNotificationAddress;
	}

	/**
	 * @param emailNotificationAddress
	 *            the emailNotificationAddress to set
	 */
	public void setEmailNotificationAddress(String emailNotificationAddress) {
		this.emailNotificationAddress = emailNotificationAddress;
	}

	/**
	 * @return the smsToEmailActive
	 */
	public boolean isSmsToEmailActive() {
		return smsToEmailActive;
	}

	/**
	 * @param smsToEmailActive
	 *            the smsToEmailActive to set
	 */
	public void setSmsToEmailActive(boolean smsToEmailActive) {
		this.smsToEmailActive = smsToEmailActive;
	}

	/**
	 * @return the smsToEmailSubject
	 */
	public boolean isSmsToEmailSubject() {
		return smsToEmailSubject;
	}

	/**
	 * @param smsToEmailSubject
	 *            the smsToEmailSubject to set
	 */
	public void setSmsToEmailSubject(boolean smsToEmailSubject) {
		this.smsToEmailSubject = smsToEmailSubject;
	}

	/**
	 * @return the missedToEmail
	 */
	public boolean isMissedToEmail() {
		return missedToEmail;
	}

	/**
	 * @param missedToEmail
	 *            the missedToEmail to set
	 */
	public void setMissedToEmail(boolean missedToEmail) {
		this.missedToEmail = missedToEmail;
	}

	/**
	 * @return the showTranscripts
	 */
	public boolean isShowTranscripts() {
		return showTranscripts;
	}

	/**
	 * @param showTranscripts
	 *            the showTranscripts to set
	 */
	public void setShowTranscripts(boolean showTranscripts) {
		this.showTranscripts = showTranscripts;
	}

	/**
	 * @return the directConnect
	 */
	public boolean isDirectConnect() {
		return directConnect;
	}

	/**
	 * @param directConnect
	 *            the directConnect to set
	 */
	public void setDirectConnect(boolean directConnect) {
		this.directConnect = directConnect;
	}

	/**
	 * @return the directRtp
	 */
	public int getDirectRtp() {
		return directRtp;
	}

	/**
	 * @param directRtp
	 *            the directRtp to set
	 */
	public void setDirectRtp(int directRtp) {
		this.directRtp = directRtp;
	}

	/**
	 * @return the useDidAsSource
	 */
	public boolean isUseDidAsSource() {
		return useDidAsSource;
	}

	/**
	 * @param useDidAsSource
	 *            the useDidAsSource to set
	 */
	public void setUseDidAsSource(boolean useDidAsSource) {
		this.useDidAsSource = useDidAsSource;
	}

	/**
	 * @return the emailToSmsActive
	 */
	public boolean isEmailToSmsActive() {
		return emailToSmsActive;
	}

	/**
	 * @param emailToSmsActive
	 *            the emailToSmsActive to set
	 */
	public void setEmailToSmsActive(boolean emailToSmsActive) {
		this.emailToSmsActive = emailToSmsActive;
	}

	/**
	 * @return the missedToInbox
	 */
	public boolean isMissedToInbox() {
		return missedToInbox;
	}

	/**
	 * @param missedToInbox
	 *            the missedToInbox to set
	 */
	public void setMissedToInbox(boolean missedToInbox) {
		this.missedToInbox = missedToInbox;
	}

	/**
	 * @return the greetings
	 */
	public Greeting[] getGreetings() {
		return greetings;
	}

	/**
	 * @param greetings
	 *            the greetings to set
	 */
	public void setGreetings(Greeting[] greetings) {
		this.greetings = greetings;
	}

	/**
	 * @return the greetingsMap
	 */
	public Map<Integer, Greeting> getGreetingsMap() {
		return greetingsMap;
	}

	/**
	 * @param greetingsMap
	 *            the greetingsMap to set
	 */
	public void setGreetingsMap(Map<Integer, Greeting> greetingsMap) {
		this.greetingsMap = greetingsMap;
	}

	/**
	 * @return the activeForwardingIds
	 */
	public int[] getActiveForwardingIds() {
		return activeForwardingIds;
	}

	/**
	 * @param activeForwardingIds
	 *            the activeForwardingIds to set
	 */
	public void setActiveForwardingIds(int[] activeForwardingIds) {
		this.activeForwardingIds = activeForwardingIds;
	}

	/**
	 * @return the disabledIdMap
	 */
	public Map<Integer, Boolean> getDisabledIdMap() {
		return disabledIdMap;
	}

	/**
	 * @param disabledIdMap
	 *            the disabledIdMap to set
	 */
	public void setDisabledIdMap(Map<Integer, Boolean> disabledIdMap) {
		this.disabledIdMap = disabledIdMap;
	}

	/**
	 * @return the defaultGreetingId
	 */
	public int getDefaultGreetingId() {
		return defaultGreetingId;
	}

	/**
	 * @param defaultGreetingId
	 *            the defaultGreetingId to set
	 */
	public void setDefaultGreetingId(int defaultGreetingId) {
		this.defaultGreetingId = defaultGreetingId;
	}

	/**
	 * @return the webCallButtons
	 */
	public Object[] getWebCallButtons() {
		return webCallButtons;
	}

	/**
	 * @param webCallButtons
	 *            the webCallButtons to set
	 */
	public void setWebCallButtons(Object[] webCallButtons) {
		this.webCallButtons = webCallButtons;
	}

	/**
	 * @return the groups
	 */
	public Map<String, Group> getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(Map<String, Group> groups) {
		this.groups = groups;
	}

	/**
	 * @return the groupList
	 */
	public String[] getGroupList() {
		return groupList;
	}

	/**
	 * @param groupList
	 *            the groupList to set
	 */
	public void setGroupList(String[] groupList) {
		this.groupList = groupList;
	}

	/**
	 * @return the lowBalanceNotificationEnabled
	 */
	public boolean isLowBalanceNotificationEnabled() {
		return lowBalanceNotificationEnabled;
	}

	/**
	 * @param lowBalanceNotificationEnabled
	 *            the lowBalanceNotificationEnabled to set
	 */
	public void setLowBalanceNotificationEnabled(
			boolean lowBalanceNotificationEnabled) {
		this.lowBalanceNotificationEnabled = lowBalanceNotificationEnabled;
	}

	/**
	 * @return the emailAddresses
	 */
	public String[] getEmailAddresses() {
		return emailAddresses;
	}

	/**
	 * @param emailAddresses
	 *            the emailAddresses to set
	 */
	public void setEmailAddresses(String[] emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl
	 *            the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}

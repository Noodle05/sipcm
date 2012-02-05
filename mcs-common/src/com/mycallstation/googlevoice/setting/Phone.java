/**
 * 
 */
package com.mycallstation.googlevoice.setting;

import java.io.Serializable;

/**
 * @author Wei Gao
 * 
 */
public class Phone implements Serializable {
	private static final long serialVersionUID = 8492160082174038129L;
	private int id;
	private String name;
	private String phoneNumber;
	// Should set to HOME
	private PhoneType type;
	private boolean verified;
	// Should set to YES_WITHOUT_PIN
	private VoiceMailAccessPolicy policyBitmask;
	private boolean dEPRECATEDDisabled;
	private boolean telephonyVerified;
	private boolean smsEnabled;
	private String incomingAccessNumber;
	private boolean voicemailForwardingVerified;
	private int behaviorOnRedirect;
	private String carrier;
	private int customOverrideState;
	private boolean inVerification;
	private boolean recentlyProvisionedOrDeprovisioned;
	private String formattedNumber;
	private TimeRange wd;
	private TimeRange we;
	// Should set FALSE
	private ScheduleSet scheduleSet;
	// This is kind of duplicate with wd
	private boolean weekdayAllDay;
	private Time[] weekdayTimes;
	// This is kind of duplicate with we
	private boolean weekendAllDay;
	private Time[] weekendTimes;
	private boolean redirectToVoicemail;
	private boolean active;
	private boolean enabledForOthers;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the type
	 */
	public PhoneType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(PhoneType type) {
		this.type = type;
	}

	/**
	 * @return the verified
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * @param verified
	 *            the verified to set
	 */
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	/**
	 * @return the policyBitmask
	 */
	public VoiceMailAccessPolicy getPolicyBitmask() {
		return policyBitmask;
	}

	/**
	 * @param policyBitmask
	 *            the policyBitmask to set
	 */
	public void setPolicyBitmask(VoiceMailAccessPolicy policyBitmask) {
		this.policyBitmask = policyBitmask;
	}

	/**
	 * @return the dEPRECATEDDisabled
	 */
	public boolean isdEPRECATEDDisabled() {
		return dEPRECATEDDisabled;
	}

	/**
	 * @param dEPRECATEDDisabled
	 *            the dEPRECATEDDisabled to set
	 */
	public void setdEPRECATEDDisabled(boolean dEPRECATEDDisabled) {
		this.dEPRECATEDDisabled = dEPRECATEDDisabled;
	}

	/**
	 * @return the telephonyVerified
	 */
	public boolean isTelephonyVerified() {
		return telephonyVerified;
	}

	/**
	 * @param telephonyVerified
	 *            the telephonyVerified to set
	 */
	public void setTelephonyVerified(boolean telephonyVerified) {
		this.telephonyVerified = telephonyVerified;
	}

	/**
	 * @return the smsEnabled
	 */
	public boolean isSmsEnabled() {
		return smsEnabled;
	}

	/**
	 * @param smsEnabled
	 *            the smsEnabled to set
	 */
	public void setSmsEnabled(boolean smsEnabled) {
		this.smsEnabled = smsEnabled;
	}

	/**
	 * @return the incomingAccessNumber
	 */
	public String getIncomingAccessNumber() {
		return incomingAccessNumber;
	}

	/**
	 * @param incomingAccessNumber
	 *            the incomingAccessNumber to set
	 */
	public void setIncomingAccessNumber(String incomingAccessNumber) {
		this.incomingAccessNumber = incomingAccessNumber;
	}

	/**
	 * @return the voicemailForwardingVerified
	 */
	public boolean isVoicemailForwardingVerified() {
		return voicemailForwardingVerified;
	}

	/**
	 * @param voicemailForwardingVerified
	 *            the voicemailForwardingVerified to set
	 */
	public void setVoicemailForwardingVerified(
			boolean voicemailForwardingVerified) {
		this.voicemailForwardingVerified = voicemailForwardingVerified;
	}

	/**
	 * @return the behaviorOnRedirect
	 */
	public int getBehaviorOnRedirect() {
		return behaviorOnRedirect;
	}

	/**
	 * @param behaviorOnRedirect
	 *            the behaviorOnRedirect to set
	 */
	public void setBehaviorOnRedirect(int behaviorOnRedirect) {
		this.behaviorOnRedirect = behaviorOnRedirect;
	}

	/**
	 * @return the carrier
	 */
	public String getCarrier() {
		return carrier;
	}

	/**
	 * @param carrier
	 *            the carrier to set
	 */
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	/**
	 * @return the customOverrideState
	 */
	public int getCustomOverrideState() {
		return customOverrideState;
	}

	/**
	 * @param customOverrideState
	 *            the customOverrideState to set
	 */
	public void setCustomOverrideState(int customOverrideState) {
		this.customOverrideState = customOverrideState;
	}

	/**
	 * @return the inVerification
	 */
	public boolean isInVerification() {
		return inVerification;
	}

	/**
	 * @param inVerification
	 *            the inVerification to set
	 */
	public void setInVerification(boolean inVerification) {
		this.inVerification = inVerification;
	}

	/**
	 * @return the recentlyProvisionedOrDeprovisioned
	 */
	public boolean isRecentlyProvisionedOrDeprovisioned() {
		return recentlyProvisionedOrDeprovisioned;
	}

	/**
	 * @param recentlyProvisionedOrDeprovisioned
	 *            the recentlyProvisionedOrDeprovisioned to set
	 */
	public void setRecentlyProvisionedOrDeprovisioned(
			boolean recentlyProvisionedOrDeprovisioned) {
		this.recentlyProvisionedOrDeprovisioned = recentlyProvisionedOrDeprovisioned;
	}

	/**
	 * @return the formattedNumber
	 */
	public String getFormattedNumber() {
		return formattedNumber;
	}

	/**
	 * @param formattedNumber
	 *            the formattedNumber to set
	 */
	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	/**
	 * @return the wd
	 */
	public TimeRange getWd() {
		return wd;
	}

	/**
	 * @param wd
	 *            the wd to set
	 */
	public void setWd(TimeRange wd) {
		this.wd = wd;
	}

	/**
	 * @return the we
	 */
	public TimeRange getWe() {
		return we;
	}

	/**
	 * @param we
	 *            the we to set
	 */
	public void setWe(TimeRange we) {
		this.we = we;
	}

	/**
	 * @return the scheduleSet
	 */
	public ScheduleSet getScheduleSet() {
		return scheduleSet;
	}

	/**
	 * @param scheduleSet
	 *            the scheduleSet to set
	 */
	public void setScheduleSet(ScheduleSet scheduleSet) {
		this.scheduleSet = scheduleSet;
	}

	/**
	 * @return the weekdayAllDay
	 */
	public boolean isWeekdayAllDay() {
		return weekdayAllDay;
	}

	/**
	 * @param weekdayAllDay
	 *            the weekdayAllDay to set
	 */
	public void setWeekdayAllDay(boolean weekdayAllDay) {
		this.weekdayAllDay = weekdayAllDay;
	}

	/**
	 * @return the weekdayTimes
	 */
	public Time[] getWeekdayTimes() {
		return weekdayTimes;
	}

	/**
	 * @param weekdayTimes
	 *            the weekdayTimes to set
	 */
	public void setWeekdayTimes(Time[] weekdayTimes) {
		this.weekdayTimes = weekdayTimes;
	}

	/**
	 * @return the weekendAllDay
	 */
	public boolean isWeekendAllDay() {
		return weekendAllDay;
	}

	/**
	 * @param weekendAllDay
	 *            the weekendAllDay to set
	 */
	public void setWeekendAllDay(boolean weekendAllDay) {
		this.weekendAllDay = weekendAllDay;
	}

	/**
	 * @return the weekendTimes
	 */
	public Time[] getWeekendTimes() {
		return weekendTimes;
	}

	/**
	 * @param weekendTimes
	 *            the weekendTimes to set
	 */
	public void setWeekendTimes(Time[] weekendTimes) {
		this.weekendTimes = weekendTimes;
	}

	/**
	 * @return the redirectToVoicemail
	 */
	public boolean isRedirectToVoicemail() {
		return redirectToVoicemail;
	}

	/**
	 * @param redirectToVoicemail
	 *            the redirectToVoicemail to set
	 */
	public void setRedirectToVoicemail(boolean redirectToVoicemail) {
		this.redirectToVoicemail = redirectToVoicemail;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the enabledForOthers
	 */
	public boolean isEnabledForOthers() {
		return enabledForOthers;
	}

	/**
	 * @param enabledForOthers
	 *            the enabledForOthers to set
	 */
	public void setEnabledForOthers(boolean enabledForOthers) {
		this.enabledForOthers = enabledForOthers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Phone[id=").append(id).append(",formated number=")
				.append(formattedNumber).append("]");
		return sb.toString();
	}
}

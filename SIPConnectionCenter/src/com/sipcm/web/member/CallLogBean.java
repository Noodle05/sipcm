/**
 * 
 */
package com.sipcm.web.member;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectBoolean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;

import org.primefaces.model.LazyDataModel;

import com.sipcm.common.model.User;
import com.sipcm.sip.model.CallLog;
import com.sipcm.web.util.JSFUtils;
import com.sipcm.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "callLogBean")
@ViewScoped
public class CallLogBean implements Serializable {
	private static final long serialVersionUID = -5169706526984150588L;

	public static final String CALLTYPE_INCOMING_KEY = "member.calllog.type.incoming";
	public static final String CALLTYPE_OUTGOING_KEY = "member.calllog.type.outgoing";
	public static final String CALLTYPE_BOTH_KEY = "member.calllog.type.both";

	public static final String CALLTYPE_INCOMING = "Incoming";
	public static final String CALLTYPE_OUTGOING = "Outgoing";
	public static final String CALLTYPE_BOTH = "Both";

	private CallLogLazyDataModel lazyModel = new CallLogLazyDataModel();

	private CallLog selectedCallLog;

	private Date startDate;

	private Date endDate;

	private boolean includeConnected;

	private boolean includeFailed;

	private boolean includeCancelled;

	private String callType;

	@PostConstruct
	public void init() {
		User user = JSFUtils.getCurrentUser();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		startDate = c.getTime();
		endDate = null;
		includeConnected = true;
		includeFailed = false;
		includeCancelled = false;
		callType = CALLTYPE_BOTH;
		lazyModel.init(user, startDate, endDate, includeConnected,
				includeFailed, includeCancelled, callType);
	}

	public void updateFilter(ActionEvent actionEvent) {
		lazyModel.init(JSFUtils.getCurrentUser(), startDate, endDate,
				includeConnected, includeFailed, includeCancelled, callType);
		FacesMessage message = Messages.getMessage(
				"member.call.log.search.updated", FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void validateStatus(ComponentSystemEvent event) {
		UIComponent components = event.getComponent();
		UISelectBoolean connected = (UISelectBoolean) components
				.findComponent("includeConnected");
		UISelectBoolean failed = (UISelectBoolean) components
				.findComponent("includeFailed");
		UISelectBoolean cancelled = (UISelectBoolean) components
				.findComponent("includeCancelled");
		if (!connected.isSelected() && !failed.isSelected()
				&& !cancelled.isSelected()) {
			FacesMessage message = Messages.getMessage(
					"member.call.log.status.required",
					FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			FacesContext.getCurrentInstance().renderResponse();
		}
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<CallLog> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param selectedCallLog
	 *            the selectedCallLog to set
	 */
	public void setSelectedCallLog(CallLog selectedCallLog) {
		this.selectedCallLog = selectedCallLog;
	}

	/**
	 * @return the selectedCallLog
	 */
	public CallLog getSelectedCallLog() {
		return selectedCallLog;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param includeConnected
	 *            the includeConnected to set
	 */
	public void setIncludeConnected(boolean includeConnected) {
		this.includeConnected = includeConnected;
	}

	/**
	 * @return the includeConnected
	 */
	public boolean isIncludeConnected() {
		return includeConnected;
	}

	/**
	 * @param includeFailed
	 *            the includeFailed to set
	 */
	public void setIncludeFailed(boolean includeFailed) {
		this.includeFailed = includeFailed;
	}

	/**
	 * @return the includeFailed
	 */
	public boolean isIncludeFailed() {
		return includeFailed;
	}

	/**
	 * @param includeCancelled
	 *            the includeCancelled to set
	 */
	public void setIncludeCancelled(boolean includeCancelled) {
		this.includeCancelled = includeCancelled;
	}

	/**
	 * @return the includeCancelled
	 */
	public boolean isIncludeCancelled() {
		return includeCancelled;
	}

	/**
	 * @param callType
	 *            the callType to set
	 */
	public void setCallType(String callType) {
		this.callType = callType;
	}

	/**
	 * @return the callType
	 */
	public String getCallType() {
		return callType;
	}

	public SelectItem[] getCallTypes() {
		SelectItem[] types = new SelectItem[3];
		types[0] = new SelectItem(CALLTYPE_INCOMING, Messages.getString(null,
				CALLTYPE_INCOMING_KEY, null));
		types[1] = new SelectItem(CALLTYPE_OUTGOING, Messages.getString(null,
				CALLTYPE_OUTGOING_KEY, null));
		types[2] = new SelectItem(CALLTYPE_BOTH, Messages.getString(null,
				CALLTYPE_BOTH_KEY, null));
		return types;
	}
}

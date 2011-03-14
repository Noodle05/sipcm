/**
 * 
 */
package com.mycallstation.web.member;

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

import org.primefaces.model.LazyDataModel;

import com.mycallstation.common.model.User;
import com.mycallstation.sip.model.CallLog;
import com.mycallstation.web.util.JSFUtils;
import com.mycallstation.web.util.Messages;

/**
 * @author wgao
 * 
 */
@ManagedBean(name = "callLogBean")
@ViewScoped
public class CallLogBean implements Serializable {
	private static final long serialVersionUID = -5169706526984150588L;

	private CallLogLazyDataModel lazyModel = new CallLogLazyDataModel();

	private CallLog selectedCallLog;

	private Date startDate;

	private Date endDate;

	private boolean includeInConnected;

	private boolean includeInFailed;

	private boolean includeInCanceled;

	private boolean includeOutConnected;

	private boolean includeOutFailed;

	private boolean includeOutCanceled;

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
		includeInConnected = true;
		includeInFailed = false;
		includeInCanceled = true;
		includeOutConnected = true;
		includeOutFailed = true;
		includeOutCanceled = false;
		lazyModel.init(user, startDate, endDate, includeInConnected,
				includeInFailed, includeInCanceled, includeOutConnected,
				includeOutFailed, includeOutCanceled);
	}

	public void updateFilter(ActionEvent actionEvent) {
		lazyModel.init(JSFUtils.getCurrentUser(), startDate, endDate,
				includeInConnected, includeInFailed, includeInCanceled,
				includeOutConnected, includeOutFailed, includeOutCanceled);
		FacesMessage message = Messages.getMessage(
				"member.call.log.search.updated", FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void validateStatus(ComponentSystemEvent event) {
		UIComponent components = event.getComponent();
		UISelectBoolean inConnected = (UISelectBoolean) components
				.findComponent("includeInConnected");
		UISelectBoolean inFailed = (UISelectBoolean) components
				.findComponent("includeInFailed");
		UISelectBoolean inCanceled = (UISelectBoolean) components
				.findComponent("includeInCanceled");
		UISelectBoolean outConnected = (UISelectBoolean) components
				.findComponent("includeOutConnected");
		UISelectBoolean outFailed = (UISelectBoolean) components
				.findComponent("includeOutFailed");
		UISelectBoolean outCanceled = (UISelectBoolean) components
				.findComponent("includeOutCanceled");
		if (!inConnected.isSelected() && !inFailed.isSelected()
				&& !inCanceled.isSelected() && !outConnected.isSelected()
				&& !outFailed.isSelected() && !outCanceled.isSelected()) {
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
	 * @param includeInConnected
	 *            the includeInConnected to set
	 */
	public void setIncludeInConnected(boolean includeInConnected) {
		this.includeInConnected = includeInConnected;
	}

	/**
	 * @return the includeInConnected
	 */
	public boolean isIncludeInConnected() {
		return includeInConnected;
	}

	/**
	 * @param includeInFailed
	 *            the includeInFailed to set
	 */
	public void setIncludeInFailed(boolean includeInFailed) {
		this.includeInFailed = includeInFailed;
	}

	/**
	 * @return the includeInFailed
	 */
	public boolean isIncludeInFailed() {
		return includeInFailed;
	}

	/**
	 * @param includeInCanceled
	 *            the includeInCanceled to set
	 */
	public void setIncludeInCanceled(boolean includeInCanceled) {
		this.includeInCanceled = includeInCanceled;
	}

	/**
	 * @return the includeInCanceled
	 */
	public boolean isIncludeInCanceled() {
		return includeInCanceled;
	}

	/**
	 * @param includeOutConnected
	 *            the includeOutConnected to set
	 */
	public void setIncludeOutConnected(boolean includeOutConnected) {
		this.includeOutConnected = includeOutConnected;
	}

	/**
	 * @return the includeOutConnected
	 */
	public boolean isIncludeOutConnected() {
		return includeOutConnected;
	}

	/**
	 * @param includeOutFailed
	 *            the includeOutFailed to set
	 */
	public void setIncludeOutFailed(boolean includeOutFailed) {
		this.includeOutFailed = includeOutFailed;
	}

	/**
	 * @return the includeOutFailed
	 */
	public boolean isIncludeOutFailed() {
		return includeOutFailed;
	}

	/**
	 * @param includeOutCanceled
	 *            the includeOutCanceled to set
	 */
	public void setIncludeOutCanceled(boolean includeOutCanceled) {
		this.includeOutCanceled = includeOutCanceled;
	}

	/**
	 * @return the includeOutCanceled
	 */
	public boolean isIncludeOutCanceled() {
		return includeOutCanceled;
	}
}

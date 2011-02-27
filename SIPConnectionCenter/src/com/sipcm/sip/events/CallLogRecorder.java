/**
 * 
 */
package com.sipcm.sip.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sipcm.sip.CallStatus;
import com.sipcm.sip.CallType;
import com.sipcm.sip.business.CallLogService;
import com.sipcm.sip.model.CallLog;

/**
 * @author wgao
 * 
 */
@Component("sipCallLogRecorder")
public class CallLogRecorder implements CallEventListener {
	private static final Logger logger = LoggerFactory
			.getLogger(CallLogRecorder.class);

	@Resource(name = "callLogService")
	private CallLogService callLogService;

	@Resource(name = "sipCallLogSaveTask")
	private Runnable saveTask;

	private final Collection<CallLog> callLogs;
	private final Lock callLogsLock;

	public CallLogRecorder() {
		callLogs = new ArrayList<CallLog>();
		callLogsLock = new ReentrantLock();
	}

	public void saveCallLogs() {
		while (!callLogs.isEmpty()) {
			Collection<CallLog> entities = null;
			callLogsLock.lock();
			try {
				if (!callLogs.isEmpty()) {
					entities = new ArrayList<CallLog>(callLogs);
					callLogs.clear();
				}
			} finally {
				callLogsLock.unlock();
			}
			if (entities != null && !entities.isEmpty()) {
				callLogService.saveEntities(entities);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallStart(com.sipcm.sip
	 * .events.CallStartEvent)
	 */
	@Override
	public void outgoingCallStart(CallStartEvent event) {
		if (logger.isInfoEnabled()) {
			if (event.getAccount() != null) {
				logger.info("{} is calling {} by using account {}.",
						new Object[] {
								event.getUserSipProfile().getDisplayName(),
								event.getPartner(),
								event.getAccount().getName() });
			} else {
				logger.info("{} is calling {}.", event.getUserSipProfile()
						.getDisplayName(), event.getPartner());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallStart(com.sipcm.sip
	 * .events.CallStartEvent)
	 */
	@Override
	public void incomingCallStart(CallStartEvent event) {
		if (event.getAccount() != null) {
			logger.info("{} get call from {} though account {}.",
					new Object[] { event.getUserSipProfile().getDisplayName(),
							event.getPartner(), event.getAccount().getName() });
		} else {
			logger.info("{} get call from {}.", event.getUserSipProfile()
					.getDisplayName(), event.getPartner());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallEstablished(com.sipcm
	 * .sip.events.CallStartEvent)
	 */
	@Override
	public void outgoingCallEstablished(CallStartEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Outgoing call established: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallEstablished(com.sipcm
	 * .sip.events.CallStartEvent)
	 */
	@Override
	public void incomingCallEstablished(CallStartEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Incoming call established: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallEnd(com.sipcm.sip.
	 * events.CallEndEvent)
	 */
	@Override
	public void outgoingCallEnd(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("{} end call with {}.", event.getCallStartEvent()
					.getUserSipProfile().getDisplayName(), event
					.getCallStartEvent().getPartner());
		}
		CallLog callLog = callLogService.createNewEntity();
		callLog.setStartTime(event.getCallStartEvent().getStartTime());
		callLog.setEndTime(event.getEndTime());
		callLog.setVoipAccount(event.getCallStartEvent().getAccount());
		callLog.setStatus(CallStatus.SUCCESS);
		callLog.setPartner(event.getCallStartEvent().getPartner());
		callLog.setOwner(event.getCallStartEvent().getUserSipProfile());
		callLog.setType(CallType.OUTGOING);
		callLogsLock.lock();
		try {
			callLogs.add(callLog);
		} finally {
			callLogsLock.unlock();
		}
		saveTask.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallEnd(com.sipcm.sip.
	 * events.CallEndEvent)
	 */
	@Override
	public void incomingCallEnd(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("{} end call with {}.", event.getCallStartEvent()
					.getPartner(), event.getCallStartEvent()
					.getUserSipProfile().getDisplayName());
		}
		CallLog callLog = callLogService.createNewEntity();
		callLog.setStartTime(event.getCallStartEvent().getStartTime());
		callLog.setEndTime(event.getEndTime());
		callLog.setVoipAccount(event.getCallStartEvent().getAccount());
		callLog.setStatus(CallStatus.SUCCESS);
		callLog.setPartner(event.getCallStartEvent().getPartner());
		callLog.setOwner(event.getCallStartEvent().getUserSipProfile());
		callLog.setType(CallType.INCOMING);
		callLogsLock.lock();
		try {
			callLogs.add(callLog);
		} finally {
			callLogsLock.unlock();
		}
		saveTask.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallFailed(com.sipcm.sip
	 * .events.CallEndEvent)
	 */
	@Override
	public void outgoingCallFailed(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			if (event.getCallStartEvent().getAccount() != null) {
				logger.info(
						"{} call {} though account {} failed with error {}.",
						new Object[] {
								event.getCallStartEvent().getUserSipProfile()
										.getDisplayName(),
								event.getCallStartEvent().getPartner(),
								event.getCallStartEvent().getAccount()
										.getName(), event.getErrorMessage() });
			} else {
				logger.info(
						"{} call {} failed with error {}.",
						new Object[] {
								event.getCallStartEvent().getUserSipProfile()
										.getDisplayName(),
								event.getCallStartEvent().getPartner(),
								event.getErrorMessage() });
			}
		}
		CallLog callLog = callLogService.createNewEntity();
		callLog.setStartTime(event.getCallStartEvent().getStartTime());
		callLog.setEndTime(event.getEndTime());
		callLog.setVoipAccount(event.getCallStartEvent().getAccount());
		callLog.setStatus(CallStatus.FAILED);
		callLog.setPartner(event.getCallStartEvent().getPartner());
		callLog.setOwner(event.getCallStartEvent().getUserSipProfile());
		callLog.setErrorCode(event.getErrorCode());
		callLog.setErrorMessage(event.getErrorMessage());
		callLog.setType(CallType.OUTGOING);
		callLogsLock.lock();
		try {
			callLogs.add(callLog);
		} finally {
			callLogsLock.unlock();
		}
		saveTask.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallFailed(com.sipcm.sip
	 * .events.CallEndEvent)
	 */
	@Override
	public void incomingCallFailed(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			if (event.getCallStartEvent().getAccount() != null) {
				logger.info(
						"{} call {} though account {} failed with error {}.",
						new Object[] {
								event.getCallStartEvent().getPartner(),
								event.getCallStartEvent().getUserSipProfile()
										.getDisplayName(),
								event.getCallStartEvent().getAccount()
										.getName(), event.getErrorMessage() });
			} else {
				logger.info("{} call {} failed with error {}.", new Object[] {
						event.getCallStartEvent().getPartner(),
						event.getCallStartEvent().getUserSipProfile()
								.getDisplayName(), event.getErrorMessage() });
			}
		}
		CallLog callLog = callLogService.createNewEntity();
		callLog.setStartTime(event.getCallStartEvent().getStartTime());
		callLog.setEndTime(event.getEndTime());
		callLog.setVoipAccount(event.getCallStartEvent().getAccount());
		callLog.setStatus(CallStatus.FAILED);
		callLog.setPartner(event.getCallStartEvent().getPartner());
		callLog.setOwner(event.getCallStartEvent().getUserSipProfile());
		callLog.setErrorCode(event.getErrorCode());
		callLog.setErrorMessage(event.getErrorMessage());
		callLog.setType(CallType.INCOMING);
		callLogsLock.lock();
		try {
			callLogs.add(callLog);
		} finally {
			callLogsLock.unlock();
		}
		saveTask.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallCancelled(com.sipcm
	 * .sip.events.CallEndEvent)
	 */
	@Override
	public void outgoingCallCancelled(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("{} cancelled call with {}.", event.getCallStartEvent()
					.getUserSipProfile().getDisplayName(), event
					.getCallStartEvent().getPartner());
		}
		CallLog callLog = callLogService.createNewEntity();
		callLog.setStartTime(event.getCallStartEvent().getStartTime());
		callLog.setEndTime(event.getEndTime());
		callLog.setVoipAccount(event.getCallStartEvent().getAccount());
		callLog.setStatus(CallStatus.CANCELLED);
		callLog.setPartner(event.getCallStartEvent().getPartner());
		callLog.setOwner(event.getCallStartEvent().getUserSipProfile());
		callLog.setErrorCode(event.getErrorCode());
		callLog.setErrorMessage(event.getErrorMessage());
		callLog.setType(CallType.OUTGOING);
		callLogsLock.lock();
		try {
			callLogs.add(callLog);
		} finally {
			callLogsLock.unlock();
		}
		saveTask.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallCancelled(com.sipcm
	 * .sip.events.CallEndEvent)
	 */
	@Override
	public void incomingCallCancelled(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("{} cancelled call with {}.", event.getCallStartEvent()
					.getPartner(), event.getCallStartEvent()
					.getUserSipProfile().getDisplayName());
		}
		CallLog callLog = callLogService.createNewEntity();
		callLog.setStartTime(event.getCallStartEvent().getStartTime());
		callLog.setEndTime(event.getEndTime());
		callLog.setVoipAccount(event.getCallStartEvent().getAccount());
		callLog.setStatus(CallStatus.CANCELLED);
		callLog.setPartner(event.getCallStartEvent().getPartner());
		callLog.setOwner(event.getCallStartEvent().getUserSipProfile());
		callLog.setErrorCode(event.getErrorCode());
		callLog.setErrorMessage(event.getErrorMessage());
		callLog.setType(CallType.INCOMING);
		callLogsLock.lock();
		try {
			callLogs.add(callLog);
		} finally {
			callLogsLock.unlock();
		}
		saveTask.run();
	}
}

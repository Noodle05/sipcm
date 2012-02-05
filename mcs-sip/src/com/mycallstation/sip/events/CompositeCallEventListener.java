/**
 * 
 */
package com.mycallstation.sip.events;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Wei Gao
 * 
 */
@Component("sipCallEventListener")
public class CompositeCallEventListener implements CallEventListener {
	private static final Logger logger = LoggerFactory
			.getLogger(CompositeCallEventListener.class);

	@Resource(name = "sipCallEventListeners")
	private Collection<CallEventListener> listeners;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.CallEventListener#outgoingCallStart(com.
	 * mycallstation.sip .events.CallStartEvent)
	 */
	@Override
	@Async
	public void outgoingCallStart(CallStartEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.outgoingCallStart(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" outgoing call start event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.CallEventListener#incomingCallStart(com.
	 * mycallstation.sip .events.CallStartEvent)
	 */
	@Override
	@Async
	public void incomingCallStart(CallStartEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.incomingCallStart(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" incoming call start event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.CallEventListener#outgoingCallEstablished
	 * (com.mycallstation .sip.events.CallStartEvent)
	 */
	@Override
	@Async
	public void outgoingCallEstablished(CallStartEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.outgoingCallEstablished(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" outgoing call established event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.CallEventListener#incomingCallEstablished
	 * (com.mycallstation .sip.events.CallStartEvent)
	 */
	@Override
	@Async
	public void incomingCallEstablished(CallStartEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.incomingCallEstablished(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" incoming call established event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.events.CallEventListener#outgoingCallEnd(com.
	 * mycallstation.sip. events.CallEndEvent)
	 */
	@Override
	@Async
	public void outgoingCallEnd(CallEndEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.outgoingCallEnd(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" outgoing call end event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.events.CallEventListener#incomingCallEnd(com.
	 * mycallstation.sip. events.CallEndEvent)
	 */
	@Override
	@Async
	public void incomingCallEnd(CallEndEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.incomingCallEnd(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" incoming call end event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.CallEventListener#outgoingCallFailed(com
	 * .mycallstation.sip .events.CallEndEvent)
	 */
	@Override
	@Async
	public void outgoingCallFailed(CallEndEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.outgoingCallFailed(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" outgoing call failed event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.CallEventListener#incomingCallFailed(com
	 * .mycallstation.sip .events.CallEndEvent)
	 */
	@Override
	@Async
	public void incomingCallFailed(CallEndEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.incomingCallFailed(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" incoming call failed event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.CallEventListener#outgoingCallCancelled(
	 * com.mycallstation .sip.events.CallEndEvent)
	 */
	@Override
	@Async
	public void outgoingCallCancelled(CallEndEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.outgoingCallCancelled(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" outgoing call cancelled event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.CallEventListener#incomingCallCancelled(
	 * com.mycallstation .sip.events.CallEndEvent)
	 */
	@Override
	@Async
	public void incomingCallCancelled(CallEndEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (CallEventListener listener : listeners) {
				try {
					listener.incomingCallCancelled(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" incoming call cancelled event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}
}

/**
 * 
 */
package com.mycallstation.sip.util;

import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;

import org.mobicents.servlet.sip.core.session.MobicentsSipSession;
import org.mobicents.servlet.sip.message.B2buaHelperImpl;

/**
 * @author Wei Gao
 * 
 */
public class GvB2buaHelperImpl extends B2buaHelperImpl {
	private static final long serialVersionUID = 2L;

	public GvB2buaHelperImpl(B2buaHelperImpl orig) {
		this.setSipFactoryImpl(orig.getSipFactoryImpl());
		this.setSipManager(orig.getSipManager());
		this.setSessionMap(orig.getSessionMap());
	}

	@Override
	public void linkSipSessions(SipSession session1, SipSession session2) {
		if (session1 == null) {
			throw new NullPointerException("First argument is null");
		}
		if (session2 == null) {
			throw new NullPointerException("Second argument is null");
		}

		if (!((MobicentsSipSession) session1).isValidInternal()
				|| !((MobicentsSipSession) session2).isValidInternal()
				|| State.TERMINATED.equals(((MobicentsSipSession) session1)
						.getState())
				|| State.TERMINATED.equals(((MobicentsSipSession) session2)
						.getState())
				|| getSessionMap().get(
						((MobicentsSipSession) session1).getKey()) != null
				|| getSessionMap().get(
						((MobicentsSipSession) session2).getKey()) != null) {
			throw new IllegalArgumentException(
					"either of the specified sessions has been terminated "
							+ "or the sessions do not belong to the same application session or "
							+ "one or both the sessions are already linked with some other session(s)");
		}
		((MobicentsSipSession) session1).setB2buaHelper(this);
		((MobicentsSipSession) session2).setB2buaHelper(this);
		getSessionMap().put(((MobicentsSipSession) session1).getKey(),
				((MobicentsSipSession) session2).getKey());
		getSessionMap().put(((MobicentsSipSession) session2).getKey(),
				((MobicentsSipSession) session1).getKey());
		// dumpLinkedSessions();
	}
}

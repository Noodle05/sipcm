/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.CallLogDAO;
import com.sipcm.sip.model.CallLog;

/**
 * @author wgao
 * 
 */
@Component("callLogDAO")
public class CallLogDAOImpl extends AbstractDAO<CallLog, Long> implements
		CallLogDAO {
}

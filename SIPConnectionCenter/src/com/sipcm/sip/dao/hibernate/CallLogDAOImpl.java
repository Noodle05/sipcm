/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.CallLogDAO;
import com.sipcm.sip.model.CallLog;

/**
 * @author wgao
 * 
 */
@Repository("callLogDAO")
public class CallLogDAOImpl extends AbstractDAO<CallLog, Long> implements
		CallLogDAO {
}

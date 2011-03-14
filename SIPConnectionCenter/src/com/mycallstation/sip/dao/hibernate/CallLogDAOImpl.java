/**
 * 
 */
package com.mycallstation.sip.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.sip.dao.CallLogDAO;
import com.mycallstation.sip.model.CallLog;

/**
 * @author wgao
 * 
 */
@Repository("callLogDAO")
public class CallLogDAOImpl extends AbstractDAO<CallLog, Long> implements
		CallLogDAO {
}

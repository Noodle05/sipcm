/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.CallLogDAO;
import com.mycallstation.dataaccess.model.CallLog;

/**
 * @author Wei Gao
 * 
 */
@Repository("callLogDAO")
public class CallLogDAOImpl extends AbstractDAO<CallLog, Long> implements
		CallLogDAO {
}

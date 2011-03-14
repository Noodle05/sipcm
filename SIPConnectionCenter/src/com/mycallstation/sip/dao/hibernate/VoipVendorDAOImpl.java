/**
 * 
 */
package com.mycallstation.sip.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.sip.dao.VoipVendorDAO;
import com.mycallstation.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Repository("voipVendorDAO")
public class VoipVendorDAOImpl extends AbstractDAO<VoipVendor, Integer>
		implements VoipVendorDAO {

}

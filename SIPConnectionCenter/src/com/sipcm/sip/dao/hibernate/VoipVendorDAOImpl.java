/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.VoipVendorDAO;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Component("voipVendorDAO")
public class VoipVendorDAOImpl extends AbstractDAO<VoipVendor, Integer>
		implements VoipVendorDAO {

}
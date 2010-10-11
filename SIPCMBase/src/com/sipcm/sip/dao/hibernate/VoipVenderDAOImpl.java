/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.VoipVenderDAO;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Component("voipVenderDAO")
public class VoipVenderDAOImpl extends AbstractDAO<VoipVendor, Integer>
		implements VoipVenderDAO {

}

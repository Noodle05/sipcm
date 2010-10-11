/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.VoipVenderDAO;
import com.sipcm.sip.model.VoipVender;

/**
 * @author wgao
 * 
 */
@Component("voipVenderDAO")
public class VoipVenderDAOImpl extends AbstractDAO<VoipVender, Integer>
		implements VoipVenderDAO {

}

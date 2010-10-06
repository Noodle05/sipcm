/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.SIPProviderDAO;
import com.sipcm.sip.model.SIPProvider;

/**
 * @author wgao
 * 
 */
@Component("sipProviderDao")
public class SIPProviderDAOImpl extends AbstractDAO<SIPProvider, Integer>
		implements SIPProviderDAO {

}

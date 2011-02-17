/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.AddressBindingDAO;
import com.sipcm.sip.model.AddressBinding;

/**
 * @author wgao
 * 
 */
@Component("addressBindingDAO")
public class AddressBindingDAOImpl extends AbstractDAO<AddressBinding, Long>
		implements AddressBindingDAO {
}

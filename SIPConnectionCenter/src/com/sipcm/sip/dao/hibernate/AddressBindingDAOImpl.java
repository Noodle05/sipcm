/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.AddressBindingDAO;
import com.sipcm.sip.model.AddressBinding;

/**
 * @author wgao
 * 
 */
@Repository("addressBindingDAO")
public class AddressBindingDAOImpl extends AbstractDAO<AddressBinding, Long>
		implements AddressBindingDAO {
}

/**
 * 
 */
package com.mycallstation.sip.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.sip.dao.AddressBindingDAO;
import com.mycallstation.sip.model.AddressBinding;

/**
 * @author wgao
 * 
 */
@Repository("addressBindingDAO")
public class AddressBindingDAOImpl extends AbstractDAO<AddressBinding, Long>
		implements AddressBindingDAO {
}

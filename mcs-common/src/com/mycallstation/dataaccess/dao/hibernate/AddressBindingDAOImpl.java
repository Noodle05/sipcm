/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.AddressBindingDAO;
import com.mycallstation.dataaccess.model.AddressBinding;

/**
 * @author wgao
 * 
 */
@Repository("addressBindingDAO")
public class AddressBindingDAOImpl extends AbstractDAO<AddressBinding, Long>
		implements AddressBindingDAO {
}

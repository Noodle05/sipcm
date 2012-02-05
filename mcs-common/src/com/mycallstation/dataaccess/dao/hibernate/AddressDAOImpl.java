/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.AddressDAO;
import com.mycallstation.dataaccess.model.Address;

/**
 * @author Wei Gao
 * 
 */
@Repository("addressDAO")
public class AddressDAOImpl extends AbstractDAO<Address, Long> implements
		AddressDAO {
}

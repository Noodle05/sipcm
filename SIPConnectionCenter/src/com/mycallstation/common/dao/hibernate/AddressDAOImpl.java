/**
 * 
 */
package com.mycallstation.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.common.dao.AddressDAO;
import com.mycallstation.common.model.Address;

/**
 * @author Jack
 * 
 */
@Repository("addressDAO")
public class AddressDAOImpl extends AbstractDAO<Address, Long> implements
		AddressDAO {
}

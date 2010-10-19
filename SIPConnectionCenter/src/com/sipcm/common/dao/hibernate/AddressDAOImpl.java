/**
 * 
 */
package com.sipcm.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.common.dao.AddressDAO;
import com.sipcm.common.model.Address;

/**
 * @author Jack
 * 
 */
@Repository("addressDAO")
public class AddressDAOImpl extends AbstractDAO<Address, Long> implements
		AddressDAO {
}

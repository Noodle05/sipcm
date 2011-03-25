/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.VoipVendorDAO;
import com.mycallstation.dataaccess.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Repository("voipVendorDAO")
public class VoipVendorDAOImpl extends AbstractDAO<VoipVendor, Integer>
		implements VoipVendorDAO {

}

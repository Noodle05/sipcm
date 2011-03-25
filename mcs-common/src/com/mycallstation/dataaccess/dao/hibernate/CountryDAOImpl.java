/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.CountryDAO;
import com.mycallstation.dataaccess.model.Country;

/**
 * @author Jack
 * 
 */
@Repository("countryDAO")
public class CountryDAOImpl extends AbstractDAO<Country, Integer> implements
		CountryDAO {
}

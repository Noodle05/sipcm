/**
 * 
 */
package com.mycallstation.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.common.dao.CountryDAO;
import com.mycallstation.common.model.Country;

/**
 * @author Jack
 * 
 */
@Repository("countryDAO")
public class CountryDAOImpl extends AbstractDAO<Country, Integer> implements
		CountryDAO {
}

/**
 * 
 */
package com.sipcm.common.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.common.dao.CountryDAO;
import com.sipcm.common.model.Country;

/**
 * @author Jack
 * 
 */
@Repository("countryDAO")
public class CountryDAOImpl extends AbstractDAO<Country, Integer> implements
		CountryDAO {
}

/**
 * 
 */
package com.mycallstation.common.business.impl;

import java.util.Calendar;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.common.business.RegistrationInvitationService;
import com.mycallstation.common.model.RegistrationInvitation;

/**
 * @author wgao
 * 
 */
@Service("registrationInvitationService")
public class RegistrationInvitationServiceImpl extends
		AbstractService<RegistrationInvitation, Integer> implements
		RegistrationInvitationService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
	 */
	@Override
	@Resource(name = "registrationInvitationDAO")
	public void setDAO(DAO<RegistrationInvitation, Integer> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#createNewEntity()
	 */
	@Override
	public RegistrationInvitation createNewEntity() {
		RegistrationInvitation entity = super.createNewEntity();
		UUID uid = UUID.randomUUID();
		entity.setCode(uid.toString());
		entity.setCount(1);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.common.business.RegistrationInvitationService#
	 * generateInvitation (int, int)
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RegistrationInvitation generateInvitation(int count, int days) {
		RegistrationInvitation entity = createNewEntity();
		entity.setCount(count);
		if (days > 0) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, days);
			entity.setExpireDate(c.getTime());
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.common.business.RegistrationInvitationService#
	 * getInvitationByCode (java.lang.String)
	 */
	@Override
	public RegistrationInvitation getInvitationByCode(String code) {
		Filter filter = filterFactory.createSimpleFilter("code", code);
		return dao.getUniqueEntity(filter);
	}
}

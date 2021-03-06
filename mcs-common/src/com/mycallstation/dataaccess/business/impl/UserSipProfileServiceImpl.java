/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.Operator;
import com.mycallstation.constant.KeepAlivePingType;
import com.mycallstation.constant.OnlineStatus;
import com.mycallstation.constant.PhoneNumberStatus;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.dao.UserSipProfileDAO;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserSipProfile;

/**
 * @author Wei Gao
 * 
 */
@Service("userSipProfileService")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class UserSipProfileServiceImpl extends
		AbstractService<UserSipProfile, Long> implements UserSipProfileService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
	 */
	@Override
	@Resource(name = "userSipProfileDAO")
	public void setDAO(DAO<UserSipProfile, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.business.Service#createNewEntity()
	 */
	@Override
	public UserSipProfile createNewEntity() {
		UserSipProfile entity = super.createNewEntity();
		entity.setSipStatus(OnlineStatus.OFFLINE);
		entity.setPhoneNumberStatus(PhoneNumberStatus.UNVERIFIED);
		entity.setAllowLocalDirectly(true);
		entity.setKeepAliveType(KeepAlivePingType.NO);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserSipProfileService#createUserSipProfile
	 * (com .mycallstation.common.model.User)
	 */
	@Override
	public UserSipProfile createUserSipProfile(User user) {
		UserSipProfile userSipProfile = createNewEntity();
		userSipProfile.setOwner(user);
		return userSipProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserSipProfileService#getUserSipProfileByUser
	 * ( com.mycallstation.common.model.User)
	 */
	@Override
	@Transactional(readOnly = true)
	public UserSipProfile getUserSipProfileByUser(User user) {
		// Filter filter = filterFactory.createSimpleFilter("owner", user);
		// return dao.getUniqueEntity(filter);
		return dao.getEntityById(user.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserSipProfileService#
	 * getUserSipProfileByUsername (java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public UserSipProfile getUserSipProfileByUsername(String username) {
		Filter filter = filterFactory.createSimpleFilter("owner.username",
				username, Operator.IEQ);
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserSipProfileService#
	 * getUserSipProfileByPhoneNumber (java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public UserSipProfile getUserSipProfileByVerifiedPhoneNumber(
			String phoneNumber) {
		Filter f1 = filterFactory
				.createSimpleFilter("phoneNumber", phoneNumber);
		Filter f2 = filterFactory.createSimpleFilter("phoneNumberStatus",
				PhoneNumberStatus.UNVERIFIED, Operator.NOT_EQ);
		Filter filter = f1.appendAnd(f2);
		List<UserSipProfile> entities = dao.getEntities(filter, null, null);
		if (entities != null && !entities.isEmpty()) {
			return entities.iterator().next();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserSipProfileService#updateOnlineStatus
	 * (com.mycallstation .common.OnlineStatus,
	 * com.mycallstation.sip.model.UserSipProfile[])
	 */
	@Override
	@Transactional
	public void updateOnlineStatus(OnlineStatus onlineStatus,
			UserSipProfile... userSipProfiles) {
		((UserSipProfileDAO) dao).updateOnlineStatus(onlineStatus,
				userSipProfiles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.dataaccess.business.UserSipProfileService#
	 * updateLastReceiveCallTime
	 * (com.mycallstation.dataaccess.model.UserSipProfile[])
	 */
	@Override
	@Transactional
	public void updateLastReceiveCallTime(UserSipProfile... userSipProfiles) {
		((UserSipProfileDAO) dao).updateLastReceiveCallTime(userSipProfiles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserSipProfileService#
	 * checkAddressBindingExpires()
	 */
	@Override
	@Transactional
	public Collection<Long> checkAddressBindingExpires() {
		return ((UserSipProfileDAO) dao).checkAddressBindingExpires();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.dataaccess.business.UserSipProfileService#
	 * getNeedPingUserSipProfile(int, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<Long> getNeedPingUserSipProfile(int timeout,
			boolean onlineOnly) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, (0 - timeout));
		Date o = c.getTime();
		Filter filter = filterFactory.createSimpleFilter("keepAliveType",
				KeepAlivePingType.PING);
		if (onlineOnly) {
			Filter f1 = filterFactory.createSimpleFilter("sipStatus",
					OnlineStatus.ONLINE);
			filter = filter.appendAnd(f1);
		}
		Filter f1 = filterFactory.createSimpleFilter("keepAliveType",
				KeepAlivePingType.ALWAYS_PING);
		filter = filter.appendOr(f1);
		f1 = filterFactory.createSimpleFilter("phoneNumberStatus",
				PhoneNumberStatus.GOOGLEVOICEVERIFIED);
		filter = filter.appendAnd(f1);
		f1 = filterFactory.createIsNullFilter("lastReceiveCallTime");
		Filter f2 = filterFactory.createSimpleFilter("lastReceiveCallTime", o,
				Operator.LESS_THAN);
		f1 = f1.appendOr(f2);
		filter = filter.appendAnd(f1);
		return dao.getEntityIds(filter, null, null);
	}
}

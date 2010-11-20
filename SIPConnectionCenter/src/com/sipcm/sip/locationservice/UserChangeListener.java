/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.sipcm.common.model.User;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Aspect
@Order(50)
@Component("userChangeListener")
public class UserChangeListener {
	private static final Logger logger = LoggerFactory
			.getLogger(UserChangeListener.class);

	@Resource(name = "sipLocationService")
	private LocationService locationService;

	@Around(value = "target(com.sipcm.common.business.UserService) && execution(* saveEntity(..)) && args(user)")
	public Object aroundSaveUser(ProceedingJoinPoint pjp, User user)
			throws Throwable {
		boolean isNew = true;
		if (user != null) {
			if (user.getId() != null) {
				isNew = false;
			}
		}
		Object ret = pjp.proceed(new Object[] { user });
		if (!isNew) {
			if (logger.isDebugEnabled()) {
				logger.debug("Existing user: \"" + user
						+ "\" saved, notify location service.");
			}
			try {
				if (user.getStatus().isActive()) {
					locationService.onUserChanged(user.getId());
				} else {
					locationService.onUserDisabled(user.getId());
				}
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Error happened when notify location service..", e);
				}
			}
		}
		return ret;
	}

	@Around(value = "target(com.sipcm.sip.business.UserSipProfileService) && execution(* saveEntity(..)) && args(userSipProfile)")
	public Object aroundSaveUserSipProfile(ProceedingJoinPoint pjp,
			UserSipProfile userSipProfile) throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("Existing user sip profile: \"" + userSipProfile
					+ "\" saved, notify location service.");
		}
		boolean isNew = true;
		if (userSipProfile != null && userSipProfile.getId() != null) {
			isNew = false;
		}
		Object ret = pjp.proceed(new Object[] { userSipProfile });
		if (!isNew) {
			try {
				if (userSipProfile.getOwner().getStatus().isActive()) {
					locationService.onUserChanged(userSipProfile.getId());
				} else {
					locationService.onUserDisabled(userSipProfile.getId());
				}
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Error happened when notify location service.", e);
				}
			}
		}
		return ret;
	}

	@Around(value = "target(com.sipcm.common.business.UserService) && execution(* saveEntities(..)) && args(users)")
	public Object aroundSaveUsers(ProceedingJoinPoint pjp,
			Collection<User> users) throws Throwable {
		Collection<Long> modifiedUserIds;
		Collection<Long> disabledUserIds;
		if (users != null && !users.isEmpty()) {
			modifiedUserIds = new ArrayList<Long>(users.size());
			disabledUserIds = new ArrayList<Long>(users.size());
			for (User user : users) {
				if (user.getId() != null) {
					if (user.getStatus().isActive()) {
						modifiedUserIds.add(user.getId());
					} else {
						disabledUserIds.add(user.getId());
					}
				}
			}
		} else {
			modifiedUserIds = Collections.emptyList();
			disabledUserIds = Collections.emptyList();
		}
		Object ret = pjp.proceed(new Object[] { users });
		if (!modifiedUserIds.isEmpty() || !disabledUserIds.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Collection of users saved, notify location service.");
			}
			try {
				if (!modifiedUserIds.isEmpty()) {
					Long[] ids = new Long[modifiedUserIds.size()];
					ids = modifiedUserIds.toArray(ids);
					locationService.onUserChanged(ids);
				}
				if (!disabledUserIds.isEmpty()) {
					Long[] ids = new Long[disabledUserIds.size()];
					ids = disabledUserIds.toArray(ids);
					locationService.onUserDisabled(ids);
				}
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Error happened when notify location service..", e);
				}
			}
		}
		return ret;
	}

	@Around(value = "target(com.sipcm.sip.business.UserSipProfileService) && execution(* saveEntity(..)) && args(userSipProfiles)")
	public Object aroundSaveUserSipProfile(ProceedingJoinPoint pjp,
			Collection<UserSipProfile> userSipProfiles) throws Throwable {
		Collection<Long> modifiedUserIds;
		Collection<Long> disabledUserIds;
		if (userSipProfiles != null && !userSipProfiles.isEmpty()) {
			modifiedUserIds = new ArrayList<Long>(userSipProfiles.size());
			disabledUserIds = new ArrayList<Long>(userSipProfiles.size());
			for (UserSipProfile usp : userSipProfiles) {
				if (usp.getId() != null) {
					if (usp.getOwner().getStatus().isActive()) {
						modifiedUserIds.add(usp.getId());
					} else {
						disabledUserIds.add(usp.getId());
					}
				}
			}
		} else {
			modifiedUserIds = Collections.emptyList();
			disabledUserIds = Collections.emptyList();
		}
		Object ret = pjp.proceed(new Object[] { userSipProfiles });
		if (!modifiedUserIds.isEmpty() || !disabledUserIds.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Collection of user sip profiles saved, notify location service.");
			}
			try {
				if (!modifiedUserIds.isEmpty()) {
					Long[] ids = new Long[modifiedUserIds.size()];
					ids = modifiedUserIds.toArray(ids);
					locationService.onUserChanged(ids);
				}
				if (!disabledUserIds.isEmpty()) {
					Long[] ids = new Long[disabledUserIds.size()];
					ids = disabledUserIds.toArray(ids);
					locationService.onUserDisabled(ids);
				}
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Error happened when notify location service..", e);
				}
			}
		}
		return ret;
	}

	@AfterReturning(pointcut = "target(com.sipcm.common.business.UserService) && execution(* removeEntity*(..))", returning = "user")
	public void afterDeleteUser(User user) {
		if (user != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("User \"" + user
						+ "\" been deleted, notify location service.");
			}
			try {
				locationService.onUserDisabled(user.getId());
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Error happened when notify location service.", e);
				}
			}
		}
	}

	@AfterReturning(pointcut = "target(com.sipcm.sip.business.UserSipProfileService) && execution(* removeEntity*(..))", returning = "userSipProfile")
	public void afterDeleteUserSipProfile(UserSipProfile userSipProfile) {
		if (userSipProfile != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("User sip profile \"" + userSipProfile
						+ "\" been deleted, notify location service.");
			}
			try {
				locationService.onUserDisabled(userSipProfile.getId());
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Error happened when notify location service.", e);
				}
			}
		}
	}

	@AfterReturning(pointcut = "target(com.sipcm.common.business.UserService) && execution(* removeEntities(..))", returning = "users")
	public void afterDeleteUsers(Collection<User> users) {
		if (users != null && !users.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Collection of users been deleted, notify location service.");
			}
			try {
				Collection<Long> userIds = new ArrayList<Long>(users.size());
				for (User user : users) {
					userIds.add(user.getId());
				}
				Long[] ids = new Long[userIds.size()];
				ids = userIds.toArray(ids);
				locationService.onUserDisabled(ids);
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Error happened when notify location service.", e);
				}
			}
		}
	}

	@AfterReturning(pointcut = "target(com.sipcm.sip.business.UserSipProfileService) && execution(* removeEntities(..))", returning = "userSipProfiles")
	public void afterDeleteUserSipProfiles(
			Collection<UserSipProfile> userSipProfiles) {
		if (userSipProfiles != null && !userSipProfiles.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Collection of user sip profiles been deleted, notify location service.");
			}
			try {
				Collection<Long> userIds = new ArrayList<Long>(
						userSipProfiles.size());
				for (UserSipProfile usp : userSipProfiles) {
					userIds.add(usp.getId());
				}
				Long[] ids = new Long[userIds.size()];
				ids = userIds.toArray(ids);
				locationService.onUserDisabled(ids);
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Error happened when notify location service.", e);
				}
			}
		}
	}
}

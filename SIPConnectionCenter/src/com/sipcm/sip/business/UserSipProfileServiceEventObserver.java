/**
 * 
 */
package com.sipcm.sip.business;

import java.util.Collection;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.sipcm.base.ServiceEventListener;
import com.sipcm.base.business.AbstractServiceEventObserver;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Aspect
@Order(50)
@Component("userSipProfileServiceEventObserver")
public class UserSipProfileServiceEventObserver extends
		AbstractServiceEventObserver<UserSipProfile, Long> {
	@Override
	@Resource(name = "userSipProfileServiceEventListener")
	protected void setListener(
			ServiceEventListener<UserSipProfile, Long> listener) {
		super.setListener(listener);
	}

	@Around("target(com.sipcm.sip.business.UserSipProfileService) && execution(* saveEntity(..)) && args(userSipProfile)")
	public final Object entitySaved(ProceedingJoinPoint pjp,
			UserSipProfile userSipProfile) throws Throwable {
		return aroundSaveEntity(pjp, userSipProfile);
	}

	@Around("target(com.sipcm.sip.business.UserSipProfileService) && execution(* saveEntity(..)) && args(userSipProfiles)")
	public final Object entitiesSaved(ProceedingJoinPoint pjp,
			Collection<UserSipProfile> userSipProfiles) throws Throwable {
		return aroundSaveEntities(pjp, userSipProfiles);
	}

	@AfterReturning(pointcut = "target(com.sipcm.sip.business.UserSipProfileService) && execution(* removeEntity*(..))", returning = "userSipProfile")
	public final void entityDeleted(UserSipProfile userSipProfile) {
		afterDeleteEntity(userSipProfile);
	}

	@AfterReturning(pointcut = "target(com.sipcm.sip.business.UserSipProfileService) && execution(public * removeEntityById*(..))", returning = "userSipProfile")
	public final void entityDeletedById(UserSipProfile userSipProfile) {
		afterDeleteEntity(userSipProfile);
	}

	@AfterReturning(pointcut = "target(com.sipcm.sip.business.UserSipProfileService) && execution(* removeEntities(..))", returning = "userSipProfiles")
	public final void entitiesDeleted(Collection<UserSipProfile> userSipProfiles) {
		afterDeleteEntities(userSipProfiles);
	}
}

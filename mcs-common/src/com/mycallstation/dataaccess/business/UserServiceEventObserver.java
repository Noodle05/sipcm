/**
 * 
 */
package com.mycallstation.dataaccess.business;

import java.util.Collection;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mycallstation.base.ServiceEventListener;
import com.mycallstation.base.business.AbstractServiceEventObserver;
import com.mycallstation.dataaccess.model.User;

/**
 * @author Wei Gao
 * 
 */
@Aspect
@Order(50)
@Component("userServiceEventObserver")
public class UserServiceEventObserver extends
		AbstractServiceEventObserver<User, Long> {
	@Override
	@Resource(name = "userServiceEventListener")
	protected void setListener(ServiceEventListener<User, Long> listener) {
		super.setListener(listener);
	}

	@Around("target(com.mycallstation.dataaccess.business.UserService) && execution(public * saveEntity(..)) && args(user)")
	public final Object entitySaved(ProceedingJoinPoint pjp, User user)
			throws Throwable {
		return aroundSaveEntity(pjp, user);
	}

	@Around("target(com.mycallstation.dataaccess.business.UserService) && execution(public * saveEntities(..)) && args(users)")
	public final Object entitiesSaved(ProceedingJoinPoint pjp,
			Collection<User> users) throws Throwable {
		return aroundSaveEntities(pjp, users);
	}

	@AfterReturning(pointcut = "target(com.mycallstation.dataaccess.business.UserService) && execution(public * removeEntity*(..))", returning = "user")
	public final void entityDeleted(User user) {
		afterDeleteEntity(user);
	}

	@AfterReturning(pointcut = "target(com.mycallstation.dataaccess.business.UserService) && execution(public * removeEntityById*(..))", returning = "user")
	public final void entityDeletedById(User user) {
		afterDeleteEntity(user);
	}

	@AfterReturning(pointcut = "target(com.mycallstation.dataaccess.business.UserService) && execution(public * removeEntities(..))", returning = "users")
	public final void entitiesDeleted(Collection<User> users) {
		afterDeleteEntities(users);
	}
}

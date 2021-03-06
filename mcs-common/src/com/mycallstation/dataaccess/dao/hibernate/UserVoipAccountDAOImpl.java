/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.dataaccess.dao.UserVoipAccountDAO;
import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * @author Wei Gao
 * 
 */
@Repository("userVoipAccountDAO")
public class UserVoipAccountDAOImpl extends AbstractDAO<UserVoipAccount, Long>
		implements UserVoipAccountDAO {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.dao.UserVoipAccountDAO#updateOnlineStatus(com.
	 * mycallstation.sip .model.UserVoipAccount)
	 */
	@Override
	public void updateRegisterExpires(final UserVoipAccount account) {
		getHibernateTemplate().execute(new HibernateCallback<Void>() {
			@Override
			public Void doInHibernate(Session session)
					throws HibernateException {
				Query query = session
						.createQuery("UPDATE UserVoipAccount SET register_expires = :regExpires, last_check = :lastCheck, error_code = :errorCode, error_message = :errorMessage WHERE id = :id");
				query.setLong("id", account.getId());
				query.setParameter("regExpires", account.getRegExpires(),
						StandardBasicTypes.INTEGER);
				query.setParameter("lastCheck", account.getLastCheck(),
						StandardBasicTypes.INTEGER);
				query.setInteger("errorCode", account.getErrorCode());
				query.setString("errorMessage", account.getErrorMessage());
				query.executeUpdate();
				return null;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.dao.UserVoipAccountDAO#updateAuthResponse(com.
	 * mycallstation.sip .model.UserVoipAccount)
	 */
	@Override
	public void updateAuthResponse(final UserVoipAccount account) {
		getHibernateTemplate().execute(new HibernateCallback<Void>() {
			@Override
			public Void doInHibernate(Session session)
					throws HibernateException {
				Query query = session
						.createQuery("UPDATE UserVoipAccount SET auth_response = :authResponse WHERE id = :id");
				query.setLong("id", account.getId());
				query.setParameter("authResponse", account.getAuthResponse());
				query.executeUpdate();
				return null;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.dao.UserVoipAccountDAO#
	 * updateRegisterExpiresAndAuthResponse
	 * (com.mycallstation.sip.model.UserVoipAccount)
	 */
	@Override
	public void updateRegisterExpiresAndAuthResponse(
			final UserVoipAccount account) {
		getHibernateTemplate().execute(new HibernateCallback<Void>() {
			@Override
			public Void doInHibernate(Session session)
					throws HibernateException {
				Query query = session
						.createQuery("UPDATE UserVoipAccount SET register_expires = :regExpires, last_check = :lastCheck, auth_response = :authResponse, error_code = :errorCode, error_message = :errorMessage WHERE id = :id");
				query.setLong("id", account.getId());
				query.setParameter("regExpires", account.getRegExpires(),
						StandardBasicTypes.INTEGER);
				query.setParameter("lastCheck", account.getLastCheck(),
						StandardBasicTypes.INTEGER);
				query.setParameter("authResponse", account.getAuthResponse());
				query.setInteger("errorCode", account.getErrorCode());
				query.setString("errorMessage", account.getErrorMessage());
				query.executeUpdate();
				return null;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.dao.UserVoipAccountDAO#checkRegisterExpires(int)
	 */
	@Override
	public Collection<Long> checkRegisterExpires(final int minExpires) {
		return getHibernateTemplate().execute(
				new HibernateCallback<List<Long>>() {
					@Override
					public List<Long> doInHibernate(Session session)
							throws HibernateException {
						Query query = session
								.getNamedQuery("registerClientExpires");
						query.setInteger("minExpires", minExpires);
						@SuppressWarnings("unchecked")
						List<Number> l = query.list();
						List<Long> result = new ArrayList<Long>(l.size());
						for (Number n : l) {
							result.add(n.longValue());
						}
						return result;
					}
				});
	}
}

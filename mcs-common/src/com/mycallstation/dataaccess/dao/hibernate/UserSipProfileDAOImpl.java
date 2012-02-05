/**
 * 
 */
package com.mycallstation.dataaccess.dao.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.mycallstation.base.dao.hibernate.AbstractDAO;
import com.mycallstation.constant.OnlineStatus;
import com.mycallstation.dataaccess.dao.UserSipProfileDAO;
import com.mycallstation.dataaccess.model.UserSipProfile;

/**
 * @author Wei Gao
 * 
 */
@Repository("userSipProfileDAO")
public class UserSipProfileDAOImpl extends AbstractDAO<UserSipProfile, Long>
		implements UserSipProfileDAO {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.dao.UserSipProfileDAO#updateOnlineStatus(com.
	 * mycallstation.common .OnlineStatus,
	 * com.mycallstation.sip.model.UserSipProfile[])
	 */
	@Override
	public void updateOnlineStatus(final OnlineStatus onlineStatus,
			final UserSipProfile... userSipProfiles) {
		getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session)
					throws HibernateException, SQLException {
				int ret = 0;
				String queryStr = "UPDATE UserSipProfile usp SET usp.sipStatus = :sipStatus WHERE id = :id";
				Query query = session.createQuery(queryStr);
				for (UserSipProfile userSipProfile : userSipProfiles) {
					query.setLong("id", userSipProfile.getId());
					query.setParameter("sipStatus", onlineStatus);
					ret += query.executeUpdate();
				}
				return ret;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.dataaccess.dao.UserSipProfileDAO#updateLastReceiveCallTime
	 * (com.mycallstation.dataaccess.model.UserSipProfile[])
	 */
	@Override
	public void updateLastReceiveCallTime(
			final UserSipProfile... userSipProfiles) {
		getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session)
					throws HibernateException, SQLException {
				int ret = 0;
				String queryStr = "UPDATE UserSipProfile usp SET usp.lastReceiveCallTime = :lastReceiveCallTime WHERE id = :id";
				Query query = session.createQuery(queryStr);
				for (UserSipProfile userSipProfile : userSipProfiles) {
					query.setLong("id", userSipProfile.getId());
					query.setTimestamp("lastReceiveCallTime", new Date());
					ret += query.executeUpdate();
				}
				return ret;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.dao.UserSipProfileDAO#checkAddressBindingExpires()
	 */
	@Override
	public Collection<Long> checkAddressBindingExpires() {
		return getHibernateTemplate().execute(
				new HibernateCallback<List<Long>>() {
					@Override
					public List<Long> doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.getNamedQuery("checkAddressBindingExpires");
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

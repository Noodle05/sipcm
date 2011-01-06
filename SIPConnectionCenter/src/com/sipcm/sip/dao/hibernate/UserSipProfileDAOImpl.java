/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.common.OnlineStatus;
import com.sipcm.sip.dao.UserSipProfileDAO;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Component("userSipProfileDAO")
public class UserSipProfileDAOImpl extends AbstractDAO<UserSipProfile, Long>
		implements UserSipProfileDAO {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.dao.UserSipProfileDAO#updateOnlineStatus(com.sipcm.common
	 * .OnlineStatus, com.sipcm.sip.model.UserSipProfile[])
	 */
	@Override
	public void updateOnlineStatus(final OnlineStatus onlineStatus,
			final UserSipProfile... userSipProfiles) {
		getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.springframework.orm.hibernate3.HibernateCallback#doInHibernate
			 * (org.hibernate.Session)
			 */
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
					userSipProfile.setSipStatus(onlineStatus);
				}
				return ret;
			}
		});
	}
}

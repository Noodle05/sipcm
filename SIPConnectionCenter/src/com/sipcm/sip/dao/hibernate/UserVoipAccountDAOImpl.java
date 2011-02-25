/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.UserVoipAccountDAO;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
@Repository("userVoipAccountDAO")
public class UserVoipAccountDAOImpl extends AbstractDAO<UserVoipAccount, Long>
		implements UserVoipAccountDAO {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.dao.UserVoipAccountDAO#updateOnlineStatus(com.sipcm.sip
	 * .model.UserVoipAccount)
	 */
	@Override
	public void updateOnlineStatus(final UserVoipAccount account) {
		getHibernateTemplate().execute(new HibernateCallback<Void>() {
			@Override
			public Void doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery("UPDATE UserVoipAccount SET online = :online WHERE id = :id");
				query.setLong("id", account.getId());
				query.setBoolean("online", account.isOnline());
				query.executeUpdate();
				return null;
			}
		});
	}
}

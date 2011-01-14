/**
 * 
 */
package com.sipcm.sip.dao.hibernate;

import java.sql.SQLException;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;

import com.sipcm.base.dao.Callback;
import com.sipcm.base.dao.hibernate.AbstractDAO;
import com.sipcm.sip.dao.UserSipBindingDAO;
import com.sipcm.sip.model.UserSipBinding;

/**
 * @author wgao
 * 
 */
@Component("userSipBindingDAO")
public class UserSipBindingDAOImpl extends AbstractDAO<UserSipBinding, Long>
		implements UserSipBindingDAO {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.dao.UserSipBindingDAO#goThoughAllEntity(com.sipcm.base.
	 * dao.Callback)
	 */
	@Override
	public void goThoughAllEntity(final Callback<UserSipBinding, Long> callback) {
		final UserSipBindingDAO dao = this;
		getHibernateTemplate().execute(new HibernateCallback<Long>() {
			@Override
			public Long doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criterial = session.createCriteria(getEntityName())
						.addOrder(Order.asc("id"))
						.setCacheMode(CacheMode.IGNORE);
				ScrollableResults userSipBindings = criterial
						.scroll(ScrollMode.FORWARD_ONLY);
				long ret = 0;
				while (userSipBindings.next()) {
					UserSipBinding userSipBinding = (UserSipBinding) userSipBindings
							.get(0);
					try {
						callback.execute(dao, userSipBinding);
					} catch (Throwable e) {
						if (logger.isWarnEnabled()) {
							logger.warn(
									"Error happened in callback when process entity: \"{}\".",
									userSipBinding);
						}
					} finally {
						ret++;
					}
				}
				return ret;
			}
		});
	}
}

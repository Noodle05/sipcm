/**
 * 
 */
package com.mycallstation.base.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.FSP;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.InvalidFilterException;
import com.mycallstation.base.filter.Page;
import com.mycallstation.base.filter.Sort;

/**
 * @author Jack
 * 
 */
public abstract class AbstractDAO<Entity extends Serializable, ID extends Serializable>
		extends HibernateDaoSupport implements DAO<Entity, ID> {
	public static final String DEFAULT_FILTER = "defaultFilter";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String entityName;

	protected Class<Entity> entityClass;

	protected boolean hasDefaultFilter;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		entityClass = (Class<Entity>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		assert entityClass != null;
		if (logger.isDebugEnabled()) {
			logger.debug("Entity class been initialized as: {}",
					entityClass.getName());
		}
		FilterDefs fds = AnnotationUtils.findAnnotation(entityClass,
				FilterDefs.class);
		if (fds != null) {
			for (FilterDef fd : fds.value()) {
				if (DEFAULT_FILTER.equals(fd.name())) {
					hasDefaultFilter = true;
				}
			}
		}
		if (!hasDefaultFilter) {
			FilterDef fd = AnnotationUtils.findAnnotation(entityClass,
					FilterDef.class);
			if (fd != null && DEFAULT_FILTER.equals(fd.name())) {
				hasDefaultFilter = true;
			}
		}
	}

	protected void preSessionOperation() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#getEntities()
	 */
	@Override
	public List<Entity> getEntities() {
		return getEntities(null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#getEntityIds()
	 */
	@Override
	public List<ID> getEntityIds() {
		return getEntityIds(null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.dao.DAO#getEntities(com.mycallstation.base.filter
	 * .FSP)
	 */
	@Override
	public List<Entity> getEntities(final FSP fsp) {
		Filter filter = null;
		Sort sort = null;
		Page page = null;
		if (fsp != null) {
			filter = fsp.getFilter();
			sort = fsp.getSort();
			page = fsp.getPage();
		}
		return getEntities(filter, sort, page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.dao.DAO#getEntityIds(com.mycallstation.base.filter
	 * .FSP)
	 */
	@Override
	public List<ID> getEntityIds(final FSP fsp) {
		Filter filter = null;
		Sort sort = null;
		Page page = null;
		if (fsp != null) {
			filter = fsp.getFilter();
			sort = fsp.getSort();
			page = fsp.getPage();
		}
		return getEntityIds(filter, sort, page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.dao.DAO#getEntities(com.mycallstation.base.filter
	 * .Filter, com.mycallstation.base.filter.Sort,
	 * com.mycallstation.base.filter.Page)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Entity> getEntities(final Filter filter, final Sort sort,
			final Page page) {
		preSessionOperation();

		List<Entity> ret = getHibernateTemplate().executeFind(
				new HibernateCallback<List<Entity>>() {
					@Override
					public List<Entity> doInHibernate(Session session)
							throws HibernateException, SQLException {
						if (hasDefaultFilter) {
							session.enableFilter(DEFAULT_FILTER);
						}
						return processFindEntities(session, filter, sort, page);
					}
				});
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.dao.DAO#getEntityIds(com.mycallstation.base.filter
	 * .Filter, com.mycallstation.base.filter.Sort,
	 * com.mycallstation.base.filter.Page)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ID> getEntityIds(final Filter filter, final Sort sort,
			final Page page) {
		preSessionOperation();

		List<ID> ret = getHibernateTemplate().executeFind(
				new HibernateCallback<List<ID>>() {
					@Override
					public List<ID> doInHibernate(Session session)
							throws HibernateException, SQLException {
						if (hasDefaultFilter) {
							session.enableFilter(DEFAULT_FILTER);
						}
						return processFindIds(session, filter, sort, page);
					}
				});
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<Entity> processFindEntities(Session session, Filter filter,
			Sort sort, Page page) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					"Processing find for page: \"{}\", sort: \"{}\", filter: \"{}\"",
					new Object[] { page, sort, filter });
		}
		Query[] queries = setupQuery(session, filter, sort, page);
		Query query = queries[0];
		Query countQuery = queries[1];
		if (page != null && countQuery != null) {
			long begin = System.currentTimeMillis();
			Number size = (Number) countQuery.uniqueResult();
			long end = System.currentTimeMillis();
			if (logger.isTraceEnabled()) {
				logger.trace("Query count string: \"{}\" use {}ms",
						countQuery.getQueryString(), (end - begin));
			}
			if (size != null) {
				page.setTotalRecords(size.intValue());
			}
			query.setFirstResult(page.getStartRowPosition());
			if (page.getRecordsPerPage() > 0)
				query.setMaxResults(page.getRecordsPerPage());
		}
		long begin = System.currentTimeMillis();
		List<Entity> ret = query.list();
		long end = System.currentTimeMillis();
		if (logger.isTraceEnabled()) {
			logger.trace("Query string \"{}\" use {}ms",
					query.getQueryString(), (end - begin));
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<ID> processFindIds(Session session, Filter filter, Sort sort,
			Page page) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					"Processing find for page: \"{}\", sort: \"{}\", filter: \"{}\"",
					new Object[] { page, sort, filter });
		}
		Query[] queries = setupQuery(session, filter, sort, page, false, true);
		Query query = queries[0];
		Query countQuery = queries[1];
		if (page != null && countQuery != null) {
			long begin = System.currentTimeMillis();
			Number size = (Number) countQuery.uniqueResult();
			long end = System.currentTimeMillis();
			if (logger.isTraceEnabled()) {
				logger.trace("Query count string: \"{}\" use {}ms",
						countQuery.getQueryString(), (end - begin));
			}
			if (size != null) {
				page.setTotalRecords(size.intValue());
			}
			query.setFirstResult(page.getStartRowPosition());
			if (page.getRecordsPerPage() > 0)
				query.setMaxResults(page.getRecordsPerPage());
		}
		long begin = System.currentTimeMillis();
		List<ID> ret = query.list();
		long end = System.currentTimeMillis();
		if (logger.isTraceEnabled()) {
			logger.trace("Query string \"{}\" use {}ms",
					query.getQueryString(), (end - begin));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.dao.DAO#getUniqueEntity(com.mycallstation.base
	 * .filter.Filter)
	 */
	@Override
	public Entity getUniqueEntity(final Filter filter) {
		preSessionOperation();
		HibernateTemplate ht = getHibernateTemplate();

		Entity ret = ht.execute(new HibernateCallback<Entity>() {
			@Override
			public Entity doInHibernate(Session session)
					throws HibernateException, SQLException {
				if (hasDefaultFilter) {
					session.enableFilter(DEFAULT_FILTER);
				}
				return processFindUnique(session, filter);
			}
		});
		return ret;
	}

	@SuppressWarnings("unchecked")
	private Entity processFindUnique(Session session, Filter filter) {
		if (logger.isTraceEnabled()) {
			logger.trace("Processing find unique for filter: \"{}\"", filter);
		}
		Query[] queries = setupQuery(session, filter, null, null);
		Query query = queries[0];
		long begin = System.currentTimeMillis();
		Entity ret = (Entity) query.uniqueResult();
		long end = System.currentTimeMillis();
		if (logger.isTraceEnabled()) {
			logger.trace("Query string \"{}\" use {}ms",
					query.getQueryString(), (end - begin));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.dao.DAO#getRowCount(com.mycallstation.base.filter
	 * .Filter)
	 */
	@Override
	public int getRowCount(final Filter filter) {
		preSessionOperation();
		HibernateTemplate ht = getHibernateTemplate();

		Number count = ht.execute(new HibernateCallback<Number>() {
			@Override
			public Number doInHibernate(Session session)
					throws HibernateException, SQLException {
				if (hasDefaultFilter) {
					session.enableFilter(DEFAULT_FILTER);
				}
				return processRowCount(session, filter);
			}
		});
		return count.intValue();
	}

	private Number processRowCount(Session session, Filter filter) {
		if (logger.isTraceEnabled()) {
			logger.trace("Processing row count for filter: \"{}\"", filter);
		}
		Query[] queries = setupQuery(session, filter, null, null, true, true);
		Query countQuery = queries[1];
		Number size = (Number) countQuery.uniqueResult();
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#getEntityById(java.io.Serializable)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Entity getEntityById(ID id) {
		Entity entity = (Entity) getHibernateTemplate()
				.get(getEntityName(), id);

		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#saveEntity(java.io.Serializable)
	 */
	@Override
	public Entity saveEntity(Entity entity) {
		if (entity != null) {
			getHibernateTemplate().saveOrUpdate(entity);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#saveEntities(java.util.Collection)
	 */
	@Override
	public Collection<Entity> saveEntities(Collection<Entity> entities) {
		if (entities != null) {
			getHibernateTemplate().saveOrUpdateAll(entities);
		}
		return entities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.dao.DAO#removeEntityById(java.io.Serializable)
	 */
	@Override
	public Entity removeEntityById(ID id) {
		Entity entity = getEntityById(id);
		if (entity != null) {
			getHibernateTemplate().delete(entity);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#removeEntity(java.io.Serializable)
	 */
	@Override
	public Entity removeEntity(Entity entity) {
		if (entity != null) {
			if (!getHibernateTemplate().contains(entity)) {
				try {
					entity = getHibernateTemplate().merge(entity);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Can not merge object into hibernate session.",
								e);
					}
				}
			}
			getHibernateTemplate().delete(entity);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#removeEntities(java.util.Collection)
	 */
	@Override
	public Collection<Entity> removeEntities(Collection<Entity> entities) {
		if (entities != null) {
			getHibernateTemplate().deleteAll(entities);
		}
		return entities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#getEntityId(java.io.Serializable)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ID getEntityId(final Entity entity) {
		final SessionFactory sf = getHibernateTemplate().getSessionFactory();
		ID id = getHibernateTemplate().execute(new HibernateCallback<ID>() {
			@Override
			public ID doInHibernate(Session session) throws HibernateException,
					SQLException {
				ClassMetadata cm = sf.getClassMetadata(getEntityName());
				if (cm != null) {
					ID id = (ID) cm.getIdentifier(entity,
							(SessionImplementor) session);
					if (logger.isTraceEnabled()) {
						logger.trace("Found id for entity. Id: {}", id);
					}
					return id;
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug(
								"Entity is not manager by session factory. Entity: {}",
								entity);
					}
					return null;
				}
			}
		});
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#refreshEntity(java.io.Serializable)
	 */
	@Override
	public Entity refreshEntity(Entity entity) {
		if (entity != null) {
			getHibernateTemplate().refresh(entity);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.dao.DAO#isNewEntity(java.io.Serializable)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean isNewEntity(Entity entity) {
		ID id = getEntityId(entity);
		if (id != null) {
			Entity obj = (Entity) getHibernateTemplate().load(getEntityName(),
					id);
			if (obj != null) {
				return false;
			}
		}
		return true;
	}

	private Query[] setupQuery(Session session, Filter filter, Sort sort,
			Page page) throws HibernateException {
		return setupQuery(session, filter, sort, page, false, false);
	}

	private Query[] setupQuery(Session session, Filter filter, Sort sort,
			Page page, boolean alwaysSetupCountQuery, boolean idOnly)
			throws HibernateException {
		if (logger.isTraceEnabled()) {
			logger.trace(
					"Setting up query for page: \"{}\", sort: \"{}\", filter: \"{}\"",
					new Object[] { page, sort, filter });
		}
		try {
			String filterString = null;
			List<? extends Serializable> filterValues = null;
			String sortString = null;
			String aliasName = Filter.DEFAULT_ALIAS;

			if (filter != null) {
				filterString = filter.getString();
				filterValues = filter.getValues();
			}
			if (sort != null) {
				sortString = sort.toString();
			}

			ClassMetadata cm = session.getSessionFactory().getClassMetadata(
					getEntityName());
			String idName = cm.getIdentifierPropertyName();

			if (idOnly && idName == null) {
				throw new IllegalStateException(
						"Cannot found id property name.");
			}

			StringBuilder countSb = new StringBuilder();
			StringBuilder mainSb = new StringBuilder();
			StringBuilder bodySb = new StringBuilder();
			StringBuilder orderSb = new StringBuilder();
			if (idOnly) {
				countSb.append("select count(").append(idName).append(") ");
				mainSb.append("select ").append(idName).append(" ");
			} else {
				countSb.append("select count(*) ");
			}
			bodySb.append("from ").append(getEntityName()).append(" ")
					.append(aliasName);

			if (filterString != null && filterString.trim().length() > 0) {
				bodySb.append(" where ").append(filterString);
			}
			if (sortString != null && sortString.length() > 0) {
				orderSb.append(" order by ").append(sortString);
			}
			Query countQuery = null, query = null;
			if (alwaysSetupCountQuery || page != null) {
				String sql = countSb.append(bodySb).toString();
				if (logger.isTraceEnabled()) {
					logger.trace(
							"Creating row count query. Query string: \"{}\"",
							sql);
				}
				countQuery = session.createQuery(sql).setCacheable(true);
			}
			String sql = mainSb.append(bodySb).append(orderSb).toString();
			if (logger.isTraceEnabled()) {
				logger.trace("Creating query. Query string: \"{}\"", sql);
			}
			query = session.createQuery(sql).setCacheable(true);
			if (filterValues != null && !filterValues.isEmpty()) {
				int index = 0;
				for (Iterator<? extends Serializable> ite = filterValues
						.iterator(); ite.hasNext();) {
					Object val = ite.next();
					if (alwaysSetupCountQuery || page != null) {
						countQuery = setParameter(countQuery, index, val);
					}
					query = setParameter(query, index, val);
					index++;
				}
			}
			Query[] ret = { query, countQuery };
			return ret;
		} catch (InvalidFilterException e) {
			throw new HibernateException(e);
		}
	}

	private Query setParameter(Query query, int index, Object value) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					"Prepare parameter for query \"{}\" at index: {}, value: \"{}\"",
					new Object[] { query, index, value });
		}
		Query ret;
		if (value instanceof Boolean) {
			ret = query.setBoolean(index, ((Boolean) value).booleanValue());
		} else if (value instanceof Byte) {
			ret = query.setByte(index, ((Byte) value).byteValue());
		} else if (value instanceof Character) {
			ret = query.setCharacter(index, ((Character) value).charValue());
		} else if (value instanceof Double) {
			ret = query.setDouble(index, ((Double) value).doubleValue());
		} else if (value instanceof Float) {
			ret = query.setFloat(index, ((Float) value).floatValue());
		} else if (value instanceof Integer) {
			ret = query.setInteger(index, ((Integer) value).intValue());
		} else if (value instanceof Long) {
			ret = query.setLong(index, ((Long) value).longValue());
		} else if (value instanceof Short) {
			ret = query.setShort(index, ((Short) value).shortValue());
		} else if (value instanceof String) {
			ret = query.setString(index, (String) value);
		} else if (value instanceof byte[]) {
			ret = query.setBinary(index, (byte[]) value);
		} else if (value instanceof BigDecimal) {
			ret = query.setBigDecimal(index, (BigDecimal) value);
		} else if (value instanceof BigInteger) {
			ret = query.setBigInteger(index, (BigInteger) value);
		} else if (value instanceof Date) {
			ret = query.setDate(index, (Date) value);
		} else if (value instanceof Time) {
			ret = query.setTime(index, (Time) value);
		} else if (value instanceof Timestamp) {
			ret = query.setTimestamp(index, (Timestamp) value);
		} else if (value instanceof java.util.Date) {
			ret = query.setTimestamp(index, (java.util.Date) value);
		} else if (value instanceof Locale) {
			ret = query.setLocale(index, (Locale) value);
		} else {
			ret = query.setParameter(index, value);
		}
		return ret;
	}

	@Resource(name = "sessionFactory")
	public void setMySessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	/**
	 * @param entityName
	 *            the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the entityName
	 */
	protected String getEntityName() {
		return (entityName == null ? entityClass.getName() : entityName);
	}
}

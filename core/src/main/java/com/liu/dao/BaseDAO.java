package com.liu.dao;

import com.liu.dao.exception.DaoException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.*;

import static org.hibernate.criterion.Example.create;

@Repository
public class BaseDAO implements IBaseDAO {

    private static final Logger log = LoggerFactory.getLogger(BaseDAO.class);
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NO = 1;

    @Autowired
    protected SessionFactory sessionFactory;

    /**
     * 获得当前连接，需要在service注解事务
     *
     * @return
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 新连接
     *
     * @return
     */
    public Session getNewSession() {
        return sessionFactory.openSession();
    }

    /**
     * 保存
     *
     * @param cls
     * @param t
     * @param <T>
     */
    public <T> void save(Class<T> cls, T t) {
        log.debug("saving {} instance", cls.getSimpleName());
        try {
            getSession().save(t);
        } catch (DaoException re) {
            log.error("saving {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }

    /**
     * 删除
     *
     * @param cls
     * @param t
     * @param <T>
     */
    public <T> void delete(Class<T> cls, T t) {
        log.debug("deleting {} instance", cls.getSimpleName());
        try {
            getSession().delete(t);
        } catch (DaoException re) {
            log.error("deleting {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }

    public <T> void delete(String entityName, Serializable id) {
        log.debug("deleting {} instance", entityName);
        try {
            getSession().delete(entityName, get(entityName, id));
        } catch (DaoException re) {
            log.error("deleting {} instance failed", entityName, re);
            throw re;
        }
    }

    /**
     * 修改，bean里所有的字段都将写到update语句里，除非设置dynamic-update="true"
     *
     * @param cls
     * @param t
     * @param <T>
     */
    public <T> void update(Class<T> cls, T t) {
        log.debug("updating {} instance", cls.getSimpleName());
        try {
            getSession().update(t);
        } catch (DaoException re) {
            log.error("updating {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }

    /**
     * 修改，只会将变过的值写到update语句里，相当于设置了dynamic-update="true"
     *
     * @param cls
     * @param t
     * @param <T>
     */
    public <T> void merge(Class<T> cls, T t) {
        log.debug("merge {} instance", cls.getSimpleName());
        try {
            getSession().merge(t);
        } catch (DaoException re) {
            log.error("merge {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }

    /**
     * 保存或者修改
     *
     * @param cls
     * @param t
     * @param <T>
     */
    public <T> void saveOrUpdate(Class<T> cls, T t) {
        log.debug("saving or updating {} instance", cls.getSimpleName());
        try {
            getSession().saveOrUpdate(t);
        } catch (DaoException re) {
            log.error("saving or updating {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }

    /**
     * 根据id获得实体
     *
     * @param cls
     * @param id
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> cls, Serializable id) {
        log.debug("getting {} instance with id {}", cls.getSimpleName(), id);
        try {
            T instance = (T) getSession().get(cls, id);
            return instance;
        } catch (DaoException re) {
            log.error("getting {} instance with id {} failed", cls.getSimpleName(), id, re);
            throw re;
        }
    }

    public <T> T get(String entityName, Serializable id) {
        log.debug("getting {} instance with id {}", entityName, id);
        try {
            T instance = (T) getSession().get(entityName, id);
            return instance;
        } catch (DaoException re) {
            log.error("getting {} instance with id {} failed", entityName, id, re);
            throw re;
        }
    }

    /**
     * 根据条件获得单条记录
     *
     * @param hql
     * @param pramas
     * @param <T>
     * @return
     */
    public <T> T getUnique(String hql, Object... pramas) {
        log.debug("get unique result with hql {}, with params {}", hql, pramas);
        try {
            Query q = getSession().createQuery(hql);
            for (int i = 0; i < pramas.length; i++) {
                q.setParameter(i, pramas[i]);
            }
            T instance = (T) q.uniqueResult();
            return instance;
        } catch (DaoException re) {
            log.error("get unique result with hql {}, with params {}", hql, pramas, re);
            throw re;
        }
    }

    /**
     * 根据条件获得单条记录
     *
     * @param cls
     * @param propertyName
     * @param value
     * @param <T>
     * @return
     */
    public <T> T getUnique(Class<T> cls, String propertyName, Object value) {
        log.debug("finding {} instance with property: {}, value: {}", cls.getSimpleName(), propertyName, value);
        try {
            Query queryObject = processHql(cls.getSimpleName(), propertyName, value, null);
            return (T) queryObject.uniqueResult();
        } catch (DaoException re) {
            log.error("finding {} instance with property: {}, value: {} falied", cls.getSimpleName(), propertyName, value, re);
            throw re;
        }
    }

    /**
     * 根据多个条件获得单条记录
     * @param cls
     * @param props
     * @param <T>
     * @return
     */
    public <T> T getUniqueByProps(Class<T> cls, Map<String, Object> props) {
        log.debug("finding {} instance with properties: {}", cls.getSimpleName(), props);
        try {
            Query queryObject = processHql(cls.getSimpleName(), props, null);
            return (T) queryObject.uniqueResult();
        } catch (DaoException re) {
            log.error("finding {} instance with properties: {} falied", cls.getSimpleName(), props, re);
            throw re;
        }
    }

    /**
     * 执行增删改语句
     *
     * @param hql
     * @param pramas
     * @return
     */
    public int executeUpdate(String hql, Object... pramas) {
        log.debug("executeUpdate with hql {}, with params {}", hql, pramas);
        try {
            Query q = getSession().createQuery(hql);
            for (int i = 0; i < pramas.length; i++) {
                q.setParameter(i, pramas[i]);
            }
            return q.executeUpdate();
        } catch (DaoException re) {
            log.error("executeUpdate with hql {}, with params {}", hql, pramas, re);
            throw re;
        }
    }

    public int executeUpdate(String hql, Map<String, Object> pramas) {
        log.debug("executeUpdate with hql {}, with params {}", hql, pramas);
        try {
            Query q = getSession().createQuery(hql);
            for (String k:pramas.keySet()) {
                q.setParameter(k, pramas.get(k));
            }
            return q.executeUpdate();
        } catch (DaoException re) {
            log.error("executeUpdate with hql {}, with params {}", hql, pramas, re);
            throw re;
        }
    }

    public <T> List<T> findByProperty(Class<T> cls, String propertyName, Object value) {
        log.debug("finding {} instance with property: {}, value: {}", cls.getSimpleName(), propertyName, value);
        try {
            Query queryObject = processHql(cls.getSimpleName(), propertyName, value, null);
            return queryObject.list();
        } catch (DaoException re) {
            log.error("finding {} instance with property: {}, value: {} falied", cls.getSimpleName(), propertyName, value, re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findByProperty(Class<T> cls, String propertyName, Object value, String orderInfo) {
        log.debug("finding {} instance with property: {}, value: {}", cls.getSimpleName(), propertyName, value);
        try {
            Query queryObject = processHql(cls.getSimpleName(), propertyName, value, orderInfo);
            return queryObject.list();
        } catch (DaoException re) {
            log.error("finding {} instance with property: {}, value: {} falied", cls.getSimpleName(), propertyName, value, re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findByExample(Class<T> cls, T instance) {
        log.debug("finding {} instance by example {}", cls.getSimpleName(), instance);
        try {
            List<T> results = (List<T>) getSession().createCriteria(cls).add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (DaoException re) {
            log.error("finding {} instance by example {} falied", cls.getSimpleName(), instance, re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findByProperties(Class<T> cls, Map<String, Object> properties) {
        log.debug("finding {} instance with properties: {}", cls.getSimpleName(), properties);
        try {
            Query queryObject = processHql(cls.getSimpleName(), properties, null);
            return queryObject.list();
        } catch (DaoException re) {
            log.error("finding {} instance with properties: {} failed", cls.getSimpleName(), properties, re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findByProperties(Class<T> cls, Map<String, Object> properties, String orderInfo) {
        log.debug("finding {} instance with properties: {}", cls.getSimpleName(), properties);
        try {
            Query queryObject = processHql(cls.getSimpleName(), properties, orderInfo);
            return queryObject.list();
        } catch (DaoException re) {
            log.error("finding {} instance with properties: {} failed", cls.getSimpleName(), properties, re);
            throw re;
        }
    }

    public <T> List<T> find(String entityName, Map<String, Object> properties, int pageSize, int pageNo, String orderInfo) {
        log.debug("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", entityName, properties, orderInfo, pageSize, pageNo);
        try {
            Query queryObject = processHql(entityName, properties, orderInfo);
            pageSize = pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
            pageNo = pageNo < 1 ? DEFAULT_PAGE_NO : pageNo;
            queryObject.setFirstResult(pageSize * (pageNo - 1)).setMaxResults(pageSize);
            return queryObject.list();
        } catch (DaoException re) {
            log.error("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", entityName, properties, orderInfo, pageSize, pageNo, re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> find(Class<T> cls, Map<String, Object> properties, int pageSize, int pageNo, String orderInfo) {
        return find(cls.getSimpleName(),properties,pageSize,pageNo,orderInfo);
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> findByProperties(Class<T> cls, Map<String, Object> properties, String... columns) {
        log.debug("finding {} instance with properties: {}", cls.getSimpleName(), properties);
        try {
            StringBuilder queryString = new StringBuilder("select ");
            int columnsize = columns.length;
            for (String column : columns) {
                queryString.append("model.").append(column);
                if (columnsize > 1) queryString.append(",");
                columnsize--;
            }

            queryString.append(" from ").append(cls.getSimpleName()).append(" as model");
            queryString.append(" where ");
            int size = properties.size();
            List<Object> values = new ArrayList<>(size);
            for (String prop : properties.keySet()) {
                queryString.append("model.").append(prop).append("= ?");
                values.add(properties.get(prop));
                if (size > 1) {
                    queryString.append(" and ");
                }
                size--;
            }
            Query queryObject = getSession().createQuery(queryString.toString());
            for (int i = 0; i < values.size(); i++) {
                queryObject.setParameter(i, values.get(i));
            }
            return queryObject.list();
        } catch (DaoException re) {
            log.error("finding {} instance with properties: {} failed", cls.getSimpleName(), properties, re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> find(Class<T> cls, Map<String, Object> properties, int pageSize, int pageNo, String orderInfo, String... columns) {
        log.debug("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", cls.getSimpleName(), properties, orderInfo, pageSize, pageNo);
        try {
            StringBuilder queryString = new StringBuilder("select ");
            int columnsize = columns.length;
            for (String column : columns) {
                queryString.append("model.").append(column);
                if (columnsize > 1) queryString.append(",");
                columnsize--;
            }
            queryString.append(" from ").append(cls.getSimpleName()).append(" as model");
            List<Object> values = null;
            if (properties != null && properties.size() > 0) {
                queryString.append(" where ");
                int size = properties.size();
                values = new ArrayList<>(size);
                for (String prop : properties.keySet()) {
                    queryString.append("model.").append(prop).append("= ?");
                    values.add(properties.get(prop));
                    if (size > 1) {
                        queryString.append(" and ");
                    }
                    size--;
                }
            }
            if (!StringUtils.isEmpty(orderInfo)) {
                queryString.append(" order by ").append(orderInfo);
            }
            Query queryObject = getSession().createQuery(queryString.toString());
            if (values != null) {
                for (int i = 0; i < values.size(); i++) {
                    queryObject.setParameter(i, values.get(i));
                }
            }
            pageSize = pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
            pageNo = pageNo < 1 ? DEFAULT_PAGE_NO : pageNo;
            queryObject.setFirstResult(pageSize * (pageNo - 1)).setMaxResults(pageSize);
            return queryObject.list();
        } catch (DaoException re) {
            log.error("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", cls.getSimpleName(), properties, orderInfo, pageSize, pageNo, re);
            throw re;
        }
    }

    public <T> List<T> findByHql(String hql) {
        return findByHql(hql,null);
    }

    public <T> List<T> findByHql(String hql, Map<String, Object> properties) {
        Query query = getSession().createQuery(hql);
        if (properties != null && !properties.isEmpty()) {
            for (String key : properties.keySet()) {
                Object obj = properties.get(key);
                setParameter(query, key, obj);
            }
        }
        return query.list();
    }

    public <T> List<T> findByHqlPaging(String hql, int pagesize, int pageno) {
        return getSession().createQuery(hql).setFirstResult(pagesize * (pageno - 1)).setMaxResults(pagesize).list();
    }

    public <T> List<T> findByHqlPaging(String hql, Object[] params, int pagesize, int pageno) {
        Query query = getSession().createQuery(hql);
        if (params.length > 0) {
            int i = 0;
            for (Object param : params) {
                query.setParameter(i, param);
                i++;
            }
        }
        query.setFirstResult(pagesize * (pageno - 1));
        query.setMaxResults(pagesize);
        return query.list();
    }

    public <T> List<T> findByHqlPaging(String hql, Map<String, Object> map, int pagesize, int pageno) {
        Query query = getSession().createQuery(hql);
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                Object obj = map.get(key);
                setParameter(query, key, obj);
            }
        }
        if(pagesize>-1){
            query.setFirstResult(pagesize * (pageno - 1));
            query.setMaxResults(pagesize);
        }
        return query.list();
    }


    public <T> long count(Class<T> cls, Map<String, Object> properties) {
        StringBuilder queryString = new StringBuilder("SELECT COUNT(*) FROM ").append(cls.getSimpleName()).append(" AS MODEL");
        if (properties != null && properties.size() > 0) {
            int size = properties.size();
            queryString.append(" WHERE ");
            for (String prop : properties.keySet()) {
                queryString.append("MODEL.").append(prop).append("= :").append(prop);
                if (size > 1) {
                    queryString.append(" AND ");
                }
                size--;
            }
        }
        Query query = getSession().createQuery(queryString.toString());
        if (properties != null) {
            for (String k:properties.keySet()) {
                query.setParameter(k, properties.get(k));
            }
        }
        return (long)query.uniqueResult();
    }

    public long count(String hql, Map<String, Object> properties) {
        Query query = getSession().createQuery(hql);
        if (properties != null && !properties.isEmpty()) {
            for (String key : properties.keySet()) {
                Object obj = properties.get(key);
                setParameter(query, key, obj);
            }
        }
        return (long)query.uniqueResult();
    }

    public long countByHql(String hql, Object...params) {
        Query query = getSession().createQuery(hql);
        int i = 0;
        for (Object p : params) {
            query.setParameter(i, p);
            i++;
        }
        return (long)query.uniqueResult();
    }

    public <T> List<T> query(String entityName, String filter, int pageSize, int pageNo, String orderInfo) {
        log.debug("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", entityName, filter, orderInfo, pageSize, pageNo);
        try {
            Query queryObject = weaveHql(null, entityName, filter, orderInfo);
            pageSize = pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
            pageNo = pageNo < 1 ? DEFAULT_PAGE_NO : pageNo;
            queryObject.setFirstResult(pageSize * (pageNo - 1)).setMaxResults(pageSize);
            return queryObject.list();
        } catch (DaoException re) {
            log.error("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", entityName, filter, orderInfo, pageSize, pageNo, re);
            throw re;
        }
    }

    public long totalSize(String entity, String filter){
        Query query = weaveHql("SELECT COUNT(1)", entity, filter, null);
        return (long) query.uniqueResult();
    }

    /**
     * 拼写hql语句
     * @param queryColumns  需要查询的列 比如 "select name,sex" 或者 "select count(*)"
     * @param entity    实体名称 一般需要mapping的name
     * @param filter    过滤条件 一般由exp生成
     * @param orderInfo 排序
     * @param params    （可选）预处理的参数，会设置到filter里去
     * @return
     */
    public Query weaveHql(String queryColumns, String entity, String filter, String orderInfo, Object... params){
        StringBuilder queryString = new StringBuilder();
        if(!StringUtils.isEmpty(queryColumns)){
            queryString.append(queryColumns).append(" ");
        }
        queryString.append("FROM ").append(entity).append(" AS a");
        if(!StringUtils.isEmpty(filter)){
            queryString.append(" WHERE ").append(filter);
        }
        if(!StringUtils.isEmpty(orderInfo)){
            queryString.append(" ORDER BY ").append(orderInfo);
        }
        Query query = getSession().createQuery(queryString.toString());
        for(int i=0; i<params.length; i++){
            query.setParameter(i, params[i]);
        }
        return query;
    }

    /**
     * 拼写hql语句
     *
     * @param entity
     * @param property
     * @param value
     * @param orderInfo
     * @return
     */
    protected Query processHql(String entity, String property, Object value, String orderInfo) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(property, value);
        return processHql(entity, properties, orderInfo);
    }

    /**
     * 拼写hql语句
     *
     * @param entity
     * @param properties
     * @param orderInfo
     * @return
     */
    protected Query processHql(String entity, Map<String, Object> properties, String orderInfo) {
        StringBuilder queryString = new StringBuilder("FROM ").append(entity).append(" AS MODEL");
        if (properties != null) {
            int size = properties.size();
            if (size > 0) {
                queryString.append(" WHERE ");
                for (String prop : properties.keySet()) {
                    queryString.append("MODEL.").append(prop).append("= :").append(prop);
                    if (size > 1) {
                        queryString.append(" AND ");
                    }
                    size--;
                }
            }
        }
        if (!StringUtils.isEmpty(orderInfo)) {
            queryString.append(" ORDER BY ").append(orderInfo);
        }
        Query query = getSession().createQuery(queryString.toString());
        if (properties != null) {
            for (String k:properties.keySet()) {
                query.setParameter(k, properties.get(k));
            }
        }
        return query;
    }

    /**
     * 设置查询参数。
     *
     * @param query
     * @param name
     * @param value
     */
    private void setParameter(Query query, String name, Object value) {
        if (value instanceof Collection<?>) {
            query.setParameterList(name, (Collection<?>) value);
        } else if (value instanceof Object[]) {
            query.setParameterList(name, (Object[]) value);
        } else {
            query.setParameter(name, value);
        }
    }

}
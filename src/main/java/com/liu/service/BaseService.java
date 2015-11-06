package com.liu.service;

import com.liu.dao.BaseDAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseService {

	@Autowired
	protected BaseDAO dao;

	@Transactional
	public <T> List<T> find(Class<T> cls, Map<String, Object> properties, int pageSize, int pageNo, String orderInfo) {
		return dao.find(cls, properties, pageSize, pageNo, orderInfo);
	}

	@Transactional
	public <T> List<T> findByProperties(Class<T> cls, Map<String, Object> properties) {
		return dao.findByProperties(cls, properties);
	}

	@Transactional
	public <T> List<T> findByExample(Class<T> cls, T instance) {
		return dao.findByExample(cls, instance);
	}

	@Transactional
	public <T> List<T> findByProperty(Class<T> cls, String propertyName, Object value) {
		return dao.findByProperty(cls, propertyName, value);
	}

	@Transactional
	public <T> List<T> findByProperty(Class<T> cls, String propertyName, Object value, String orderInfo) {
		return dao.findByProperty(cls, propertyName, value,orderInfo);
	}

	@Transactional
	public <T> T get(Class<T> cls, Serializable id) {
		return dao.get(cls, id);
	}
	
	@Transactional
	public <T> void update(Class<T> cls, T t) {
		dao.update(cls, t);
	}

	@Transactional
	public <T> void merge(Class<T> cls, T t) {
		dao.merge(cls, t);
	}
	
	@Transactional
	public <T> void delete(Class<T> cls, T t) {
		dao.delete(cls, t);
	}
	
	@Transactional
	public <T> void save(Class<T> cls, T t) {
		dao.save(cls, t);
	}

    @Transactional
    public <T> long count(Class<T> cls, Map<String, Object> properties) {
        return dao.count(cls,properties);
    }

    @Transactional
    public long count(String hql, Map<String,Object> map){
        return dao.count(hql, map);
    }

	@Transactional
	public <T> T getUnique(Class<T> cls, String propertyName, Object value){
		return dao.getUnique(cls, propertyName, value);
	}

	@Transactional
	public <T> Map entityPagingByProperties(Class<T> cls, Map<String, Object> properties, int pageSize, int pageNo, String orderInfo) {
		List<T> list = dao.find(cls, properties, pageSize, pageNo, orderInfo);
		long total = 0;
		if(list != null && list.size() > 0){
			total = dao.count(cls, properties);
		}

		HashMap result = new HashMap();
		result.put("data",list);
		result.put("total", total);
		return result;
	}

	@Transactional
	public <T> Map hqlPagingByProperties(String hql, Map<String, Object> properties, int pageSize, int pageNo, String orderInfo) {
		List<T> list = dao.findByHqlPaging(
				StringUtils.isNotEmpty(orderInfo) ? hql + " order by " + orderInfo : hql, properties, pageSize, pageNo);

		long total = 0 ;
		if(list != null && list.size() > 0){
			if(hql.indexOf("select ") != -1){
				total = dao.count(new StringBuffer("select count(*) ").append(hql.substring(hql.indexOf(" from "))).toString(), properties);
			}else{
				total = dao.count(new StringBuffer("select count(*) ").append(hql).toString(), properties);
			}
		}

		HashMap result = new HashMap();
		result.put("data",list);
		result.put("total", total);
		return result;
	}
}

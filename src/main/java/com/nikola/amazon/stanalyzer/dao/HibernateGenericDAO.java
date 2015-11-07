package com.nikola.amazon.stanalyzer.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nikola.amazon.stanalyzer.exception.EntityNotFoundException;

@SuppressWarnings("unchecked")
public abstract class HibernateGenericDAO<Entity,ID extends Serializable> implements GenericDAO<Entity, ID> {
	
	public HibernateGenericDAO() {
	}
	
	protected abstract Class<?> getEntityClass();
	
	protected abstract SessionFactory getSessionFactory();
	
	@Override
	@Transactional
	public Entity getByID(ID id) throws EntityNotFoundException {
		 Entity entity = (Entity) getCurrentSession().get(getEntityClass(), id);
		 if(entity==null){
			 throw new EntityNotFoundException();
		 }
		 else return entity;
	}

	@Override
	@Transactional
	public List<Entity> getAll() {
		return getCurrentSession().createQuery("from " + getEntityClass().getName())
                .list();
	}

	@Override
	@Transactional
	public List<Entity> search(Map<String, Object> parameterMap) {
		Criteria criteria = buildSearchCriteria(parameterMap);
        return criteria.list();
	}
	
	@Override
	@Transactional
	public List<Entity> search(Map<String, Object> parameterMap,int pageSize, int startIndex) {
		Criteria criteria = buildSearchCriteria(parameterMap);
        criteria.setFirstResult(startIndex);
        criteria.setMaxResults(pageSize);
        return criteria.list();
	}
	
	
	

	@Override
	@Transactional
	public List<Entity> searchByNativeQuery(String queryName, Map<String, Object> parameterMap, int pageSize, int pageIndex) {
		Query namedQuery = getCurrentSession().getNamedQuery(queryName);
		for(Map.Entry<String, Object> parameter : parameterMap.entrySet()) {
			if(parameter.getValue() instanceof Collection) {
				namedQuery.setParameterList(parameter.getKey(), (Collection) parameter.getValue());
			}
			else if(parameter.getValue() instanceof Object[]) {
				namedQuery.setParameterList(parameter.getKey(), (Object[]) parameter.getValue());
			}
			else {
				namedQuery.setParameter(parameter.getKey(), parameter.getValue());
			}
		}
		namedQuery.setMaxResults(pageSize).setFirstResult(pageIndex*pageSize);
		return namedQuery.list();
	}

	@Override
	@Transactional
	public int count(Map<String, Object> parameterMap) {
		Criteria criteria = buildSearchCriteria(parameterMap);
        return  ((Number)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
	
	@Override
	@Transactional
	public int countNative(String countQueryName, Map<String, Object> parameterMap) {
		Query namedQuery = getCurrentSession().getNamedQuery(countQueryName);
		for(Map.Entry<String, Object> parameter : parameterMap.entrySet()) {
			if(parameter.getValue() instanceof Collection) {
				namedQuery.setParameterList(parameter.getKey(), (Collection) parameter.getValue());
			}
			else if(parameter.getValue() instanceof Object[]) {
				namedQuery.setParameterList(parameter.getKey(), (Object[]) parameter.getValue());
			}
			else {
				namedQuery.setParameter(parameter.getKey(), parameter.getValue());
			}
		}
		return ((BigInteger) namedQuery.uniqueResult()).intValueExact();
	}

	@Override
	@Transactional(propagation=Propagation.NESTED)
	public ID insert(Entity entity) {
		return (ID) getCurrentSession().save(entity);
	}

	@Override
	@Transactional
	public void update(Entity entity) {
		getCurrentSession().update(entity);
	}

	@Override
	@Transactional
	public void delete(Entity entity) {
		getCurrentSession().delete(entity);
	}

	@Override
	@Transactional
	public void deleteById(ID id) throws EntityNotFoundException {
        delete(getByID(id));
	}
	
	private Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }
	
	private Criteria buildSearchCriteria(Map<String, Object> parameterMap) {
		Criteria criteria = getCurrentSession().createCriteria(getEntityClass());
        if(parameterMap != null) {
			Set<String> fieldName = parameterMap.keySet();
	        for (String field : fieldName) {
	        	if(parameterMap.get(field) instanceof String) {
	            	String fieldvalue = parameterMap.get(field).toString();
	            	if(fieldvalue.contains("*")) {
	            		fieldvalue = fieldvalue.replace("*", "%");
	            	}
	            	else {
	            		fieldvalue = "%" + fieldvalue + "%"; 
	            	}
	                criteria.add(Restrictions.ilike(field, fieldvalue));
	        	}
	        	else if(parameterMap.get(field) instanceof List) {
	        		criteria.add(Restrictions.in(field, ((List)parameterMap.get(field)).toArray()));
	        		}
	        	}
        }
		return criteria;
	}

	@Override
	@Transactional
	public Object executeNamedQueryUniqueResult(String queryName, Map<String, Object> values) {
		Query namedQuery = getCurrentSession().getNamedQuery(queryName);
		for(Map.Entry<String, Object> entry : values.entrySet()) {
				namedQuery.setParameter(entry.getKey(), entry.getValue());
		}
		return namedQuery.uniqueResult();
	}

	@Override
	public void insertBulk(List<Entity> list) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		   
		for ( int i=0; i<list.size(); i++ ) {
		    session.save(list.get(i));
		    if ( i % 20 == 0 ) { 
		        session.flush();
		        session.clear();
		    }
		}		   
		tx.commit();
		session.close();		
	}
	
	@Override
	public void updateBulk(List<Entity> list) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		   
		for ( int i=0; i<list.size(); i++ ) {
		    session.update(list.get(i));
		    if ( i % 20 == 0 ) { 
		        session.flush();
		        session.clear();
		    }
		}		   
		tx.commit();
		session.close();
	}

	
}

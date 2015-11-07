package com.nikola.amazon.stanalyzer.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nikola.amazon.stanalyzer.entity.Stan;
import com.nikola.amazon.stanalyzer.exception.EntityNotFoundException;

@Repository("stanDao")
public class StanDao extends HibernateGenericDAO<Stan, Long> {
	
	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	protected Class<?> getEntityClass() {
		return Stan.class;
	}
	
	@Transactional
	public List<Object> getCene(Long id)  {
		List<Object> list = new ArrayList<>();
		try {
			list.addAll(getByID(id).getCene());
			return list;
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	
}

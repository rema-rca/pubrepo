package com.nikola.amazon.stanalyzer.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nikola.amazon.stanalyzer.entity.JobExecutionSummary;

@Repository
public class JobExecutionSummaryDao extends HibernateGenericDAO<JobExecutionSummary, Long> {
	
	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	public List<Object> getCene(Long id) {
		return null;
	}

	@Override
	protected Class<?> getEntityClass() {
		return JobExecutionSummary.class;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}

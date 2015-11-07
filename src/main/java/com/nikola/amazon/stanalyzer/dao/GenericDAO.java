package com.nikola.amazon.stanalyzer.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.nikola.amazon.stanalyzer.exception.EntityNotFoundException;

public interface GenericDAO<T, ID extends Serializable> {
	 
    T getByID(ID id) throws EntityNotFoundException;
 
    List<T> getAll();
 
    List<T> search(Map<String, Object> parameterMap);
    
    List<T> searchByNativeQuery(String queryName,Map<String, Object> parameterMap,int pageSize, int pageIndex);
    
    List<T> search(Map<String, Object> parameterMap,int pageSize, int pageIndex);
 
    ID insert(T entity);
    
    List<Object> getCene(ID id);
 
    void update(T entity);
 
    void delete(T entity);
 
    void deleteById(ID id) throws EntityNotFoundException;
    
    int count(Map<String, Object> parameterMap);
    
    int countNative(String countQueryName, Map<String, Object> parameterMap);
    
    Object executeNamedQueryUniqueResult(String queryName, Map<String,Object> values);
    
    void insertBulk(List<T> list);
    
    void updateBulk(List<T> list);
 
}

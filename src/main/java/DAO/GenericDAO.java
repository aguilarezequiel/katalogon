package DAO;

import java.util.List;

public interface GenericDAO<T> {
    T save(T entity);
    T update(T entity);
    void delete(T entity);
    T findById(Integer id);
    List<T> findAll();
    List<T> findAllActive();
}
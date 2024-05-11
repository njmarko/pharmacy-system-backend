package com.mrsisa.pharmacy.service;

import java.util.List;

public interface ICRUDService<T> {
    List<T> getAll();
    T get(Long id);
    T save(T entity);
    T update(T entity);
    void delete(Long id);
}

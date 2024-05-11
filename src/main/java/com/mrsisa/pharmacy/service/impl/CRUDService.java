package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.service.ICRUDService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Transactional
public abstract class CRUDService<T extends BaseEntity> implements ICRUDService<T> {

    protected abstract JpaRepository<T, Long> getEntityRepository();

    @Override
    public List<T> getAll() {
        return getEntityRepository().findAll();
    }

    @Override
    public T get(Long id) throws EntityNotFoundException {
        return findEntityChecked(id);
    }

    @Override
    public T save(T entity) {
        return getEntityRepository().save(entity);
    }

    @Override
    public T update(T entity) {
        return save(entity);
    }

    @Override
    public void delete(Long id) {
        var entity = findEntityChecked(id);
        entity.setActive(false);
    }

    private T findEntityChecked(Long id) throws EntityNotFoundException {
        var entity = getEntityRepository().findById(id).orElseThrow(() -> new NotFoundException("Cannot find entity with id: " + id));
        if (Boolean.TRUE.equals(entity.getActive())) {
            return entity;
        }
        throw new NotFoundException("Cannot find entity with id: " + id);
    }
}

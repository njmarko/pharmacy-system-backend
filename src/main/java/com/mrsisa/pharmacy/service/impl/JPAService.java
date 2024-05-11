package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.service.IJPAService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;

@Transactional
public abstract class JPAService<T extends BaseEntity> extends CRUDService<T> implements IJPAService<T> {
    @Override
    public Iterable<T> getAll(Sort sorter) {
        return getEntityRepository().findAll(sorter);
    }

    @Override
    public Page<T> getAll(Pageable page) {
        return getEntityRepository().findAll(page);
    }
}

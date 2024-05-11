package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface IJPAService<T extends BaseEntity> extends ICRUDService<T> {
    Iterable<T> getAll(Sort sorter);

    Page<T> getAll(Pageable page);

}

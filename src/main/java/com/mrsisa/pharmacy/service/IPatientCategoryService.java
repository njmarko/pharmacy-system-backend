package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.PatientCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IPatientCategoryService extends IJPAService<PatientCategory> {

    Optional<PatientCategory> findByName(String name);

    Optional<PatientCategory> findByPoints(Integer points);

    Optional<PatientCategory> findByColor(String color);

    Optional<PatientCategory> findByDiscount(Integer discount);


    Optional<PatientCategory> findById(Long id);


    PatientCategory createCategory(String name, Integer points, Integer discount, String color);

    PatientCategory updateCategory(PatientCategory category, String name, Integer points, Integer discount, String color);

    int deleteCategory(Long id);

    Page<PatientCategory> getCategories(Pageable pageable);

    PatientCategory getNextCategory(Integer points);



}

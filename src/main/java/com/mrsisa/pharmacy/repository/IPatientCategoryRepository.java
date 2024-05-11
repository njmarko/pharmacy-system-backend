package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.PatientCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPatientCategoryRepository extends JpaRepository<PatientCategory, Long> {

    Optional<PatientCategory> findByName(String name);

    Optional<PatientCategory> findByIdAndActiveTrue(Long id);

    @Query("select pc from PatientCategory pc where pc.points = :points")
    Optional<PatientCategory> findByPoints(@Param("points") Integer points);

    @Query("select pc from PatientCategory pc where pc.color = :color")
    Optional<PatientCategory> findByColor(@Param("color") String color);

    @Query("select pc from PatientCategory pc where pc.discount = :discount")
    Optional<PatientCategory> findByDiscount(@Param("discount") Integer discount);


    int deletePatientCategoryById(Long id);

    @Query("select pc from PatientCategory pc where pc.active=true")
    Page<PatientCategory> getCategories(Pageable pageable);

    @Query("select pc from PatientCategory pc where pc.points >= :points order by pc.points asc")
    List<PatientCategory> getNextCategories(@Param("points") Integer points);

    @Query("select pc from PatientCategory pc where pc.points <= :points order by pc.points desc")
    List<PatientCategory> getPossibleNewCategory(@Param("points") Integer points);


}

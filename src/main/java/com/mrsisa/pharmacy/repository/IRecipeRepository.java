package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Recipe;
import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("select count(r) from Recipe as r where r.active = true " +
            "and r.patient.id = :patientId " +
            "and r.pharmacy.id = :pharmacyId")
    Long checkIfPatientHasAnyERecipesInPharmacy(@Param("patientId") Long patientId, @Param("pharmacyId") Long pharmacyId);

    @Query("select count(r) from Recipe as r join r.reservedMedicines as item " +
            "where r.active = true " +
            "and r.patient.id = :patientId " +
            "and item.medicine.id = :drugId")
    Long checkIfPatientHasAnyERecipesWithSpecificDrug(@Param("patientId") Long patientId, @Param("drugId") Long drugId);


    @Query(value = "select r from Recipe r where r.active = true and r.patient.id = :id")
    Page<Recipe> getRecipesForPatient(@Param("id") Long id, Pageable pageable);


    @Query(value = "select rmi from RecipeMedicineInfo rmi where rmi.active = true and rmi.recipe.id = :id")
    List<RecipeMedicineInfo> getRecipeMedicine(@Param("id") Long id);


    @Query("select r from Recipe  r join fetch r.patient where r.active = true and r.id = :id")
    Optional<Recipe> getRecipeByIdAndActiveTrue(Long id);

}

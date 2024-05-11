package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRecipeMedicineInfoRepository extends JpaRepository<RecipeMedicineInfo, Long> {
    @Query(value = "select r from RecipeMedicineInfo r where r.recipe.id=:id")
    List<RecipeMedicineInfo> getMedicinesForRecipe(@Param("id") Long recipeId);
}

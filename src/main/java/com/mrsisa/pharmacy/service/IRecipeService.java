package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Recipe;
import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IRecipeService extends IJPAService<Recipe> {

    Page<Recipe> getRecipesForPatient(Long id, Pageable pageable);

    List<RecipeMedicineInfo> getRecipeMedicine(Long id);

    Optional<Recipe> getRecipeById(Long id);
}

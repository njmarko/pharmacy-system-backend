package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.Recipe;
import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import com.mrsisa.pharmacy.repository.IRecipeRepository;
import com.mrsisa.pharmacy.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService extends JPAService<Recipe> implements IRecipeService {
    private final IRecipeRepository recipeRepository;

    @Autowired
    public RecipeService(IRecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    protected JpaRepository<Recipe, Long> getEntityRepository() {
        return recipeRepository;
    }

    @Override
    public Page<Recipe> getRecipesForPatient(Long id, Pageable pageable) {
        return this.recipeRepository.getRecipesForPatient(id, pageable);
    }

    @Override
    public List<RecipeMedicineInfo> getRecipeMedicine(Long id) {
        return this.recipeRepository.getRecipeMedicine(id);
    }

    @Override
    public Optional<Recipe> getRecipeById(Long id) {
        return this.recipeRepository.getRecipeByIdAndActiveTrue(id);
    }
}

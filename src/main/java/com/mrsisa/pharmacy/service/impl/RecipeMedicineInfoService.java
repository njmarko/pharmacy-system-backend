package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import com.mrsisa.pharmacy.repository.IRecipeMedicineInfoRepository;
import com.mrsisa.pharmacy.service.IRecipeMedicineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeMedicineInfoService extends JPAService<RecipeMedicineInfo> implements IRecipeMedicineInfoService {
    private final IRecipeMedicineInfoRepository recipeMedicineInfoRepository;

    @Autowired
    public RecipeMedicineInfoService(IRecipeMedicineInfoRepository recipeMedicineInfoRepository) {
        this.recipeMedicineInfoRepository = recipeMedicineInfoRepository;
    }


    @Override
    public List<RecipeMedicineInfo> getMedicinesForRecipe(Long recipeId) {
        return this.recipeMedicineInfoRepository.getMedicinesForRecipe(recipeId);
    }

    @Override
    protected JpaRepository<RecipeMedicineInfo, Long> getEntityRepository() {
        return recipeMedicineInfoRepository;
    }
}

package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;

import java.util.List;

public interface IRecipeMedicineInfoService extends IJPAService<RecipeMedicineInfo> {
    List<RecipeMedicineInfo> getMedicinesForRecipe(Long recipeId);
}

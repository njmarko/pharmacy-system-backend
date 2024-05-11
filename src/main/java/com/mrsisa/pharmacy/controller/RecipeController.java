package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.domain.entities.Recipe;
import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import com.mrsisa.pharmacy.dto.medicine.RecipeMedicineInfoDTO;
import com.mrsisa.pharmacy.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value= "/api/recipes")
public class RecipeController {

    private final IRecipeService recipeService;

    @Autowired
    public RecipeController(IRecipeService recipeService){
        this.recipeService = recipeService;
    }


    @GetMapping("/{id}/items")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public List<RecipeMedicineInfoDTO> getRecipeItems(@PathVariable("id") Long recipeId, Principal principal){
        String name = principal.getName();
        Optional<Recipe> optionalRecipe = this.recipeService.getRecipeById(recipeId);
        if(optionalRecipe.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipe with id " + recipeId + " does not exist.");
        if(!optionalRecipe.get().getPatient().getUsername().equals(name))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this data.");
        List<RecipeMedicineInfo> infos = this.recipeService.getRecipeMedicine(recipeId);
        List<RecipeMedicineInfoDTO> dtos = new ArrayList<>();
        for(RecipeMedicineInfo info: infos){
            dtos.add(new RecipeMedicineInfoDTO(info.getMedicine().getId(), info.getQuantity(), info.getTherapyDays(), info.getPrice(), info.getMedicine().getName()));
        }
        return dtos;

    }

}

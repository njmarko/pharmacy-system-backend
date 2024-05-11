package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Recipe;
import com.mrsisa.pharmacy.dto.recipe.RecipeDTO;
import org.springframework.stereotype.Component;

@Component
public class RecipeToRecipeDTO extends AbstractConverter<Recipe, RecipeDTO>{
    @Override
    public RecipeDTO convert(Recipe recipe) {
        var dto = new RecipeDTO(recipe.getId(), recipe.getPharmacy().getId(), recipe.getPharmacy().getName(), recipe.getReservationDate());
        dto.setTotalPrice(recipe.getPrice());
        return dto;
    }
}

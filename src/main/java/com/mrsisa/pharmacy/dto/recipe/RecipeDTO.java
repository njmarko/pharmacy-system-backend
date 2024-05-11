package com.mrsisa.pharmacy.dto.recipe;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecipeDTO {

    Long recipeId;

    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    LocalDateTime reservationDate;
    Long pharmacyId;
    String pharmacyName;
    Double totalPrice;

    public RecipeDTO(Long recipeId, Long pharmacyId, String pharmacyName, LocalDateTime reservationDate){
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.recipeId = recipeId;
        this.reservationDate = reservationDate;
        this.totalPrice = 0.0;
    }
}

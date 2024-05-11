package com.mrsisa.pharmacy.dto.medicine;


import com.mrsisa.pharmacy.domain.enums.MedicineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineSearchDTO {

    private String name = "";

    @PositiveOrZero(message = "Lowest value for low grade must be a positive number or zero.")
    @Max(value = 5, message = "Highest value for low grade must be less than or equal 5.")
    private Double lowGrade = 0.0;

    @PositiveOrZero(message = "Lowest value for high grade must be a positive number or zero.")
    @Max(value = 5, message = "Highest value for high grade must be less than or equal 5.")
    private Double highGrade = 5.0;

    private Boolean issueOnRecipe;

    private MedicineType medicineType;
}

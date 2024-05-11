package com.mrsisa.pharmacy.dto.medicine;

import com.mrsisa.pharmacy.domain.enums.MedicineShape;
import com.mrsisa.pharmacy.domain.enums.MedicineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineRegistrationDTO {

    @NotBlank(message = "Medicine code cannot be blank.")
    private String code;

    @NotBlank(message = "Medicine name cannot be blank.")
    private String name;

    @NotNull(message = "Medicine shape cannot be null.")
    private MedicineShape medicineShape;

    @NotNull(message = "Medicine type cannot be null.")
    private MedicineType medicineType;

    @NotBlank(message = "Medicine composition cannot be blank.")
    private String composition;

    @NotBlank(message = "Medicine manufacturer cannot be blank.")
    private String manufacturer;

    @NotNull(message = "Medicine issue on recipe value cannot be blank.")
    private Boolean issueOnRecipe;

    @NotNull(message = "Medicine points cannot be blank.")
    @PositiveOrZero(message = "Points value cannot be negative.")
    private Integer points;

    private String additionalNotes;

    List<Long> replacements;



}

package com.mrsisa.pharmacy.dto.medicine;


import com.mrsisa.pharmacy.domain.enums.MedicineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO {
    private Long id;
    private String name;
    private String medicineCode;
    private MedicineType medicineType;
    private Double medicineAverageGrade;
    private Boolean issueOnRecipe;
}

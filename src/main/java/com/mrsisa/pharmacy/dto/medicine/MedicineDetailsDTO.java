package com.mrsisa.pharmacy.dto.medicine;


import com.mrsisa.pharmacy.domain.enums.MedicineShape;
import com.mrsisa.pharmacy.domain.enums.MedicineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDetailsDTO {

    private Long id;
    private String name;
    private MedicineType medicineType;
    private String medicineManufacturer;
    private String medicineComposition;
    private MedicineShape medicineShape;
    private Double medicineAverageGrade;
    private Boolean issueOnRecipe;
    private String additionalNotes;
    private Integer points;
}

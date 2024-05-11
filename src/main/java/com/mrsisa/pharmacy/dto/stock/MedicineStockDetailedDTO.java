package com.mrsisa.pharmacy.dto.stock;

import com.mrsisa.pharmacy.domain.enums.MedicineShape;
import com.mrsisa.pharmacy.domain.enums.MedicineType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicineStockDetailedDTO {
    private Long id;
    private String name;
    private MedicineType medicineType;
    private String manufacturer;
    private String composition;
    private MedicineShape medicineShape;
    private Double medicineAverageGrade;
    private Boolean issueOnRecipe;
    private String additionalNotes;
    private Integer points;
    private Double currentPrice;
    private Integer quantity;
}

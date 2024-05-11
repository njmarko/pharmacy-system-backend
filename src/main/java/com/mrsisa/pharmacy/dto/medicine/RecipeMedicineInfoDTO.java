package com.mrsisa.pharmacy.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecipeMedicineInfoDTO {
    private Long medicineId;
    private Integer quantity;
    private Integer therapyDays;
    private Double price;
    private String medicineName;
}

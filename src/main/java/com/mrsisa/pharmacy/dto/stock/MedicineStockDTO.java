package com.mrsisa.pharmacy.dto.stock;

import com.mrsisa.pharmacy.domain.enums.MedicineShape;
import com.mrsisa.pharmacy.domain.enums.MedicineType;
import lombok.Data;

@Data
public class MedicineStockDTO {

    private Long id;
    private String medicineName;
    private MedicineType medicineType;
    private String medicineManufacturer;
    private String medicineComposition;
    private MedicineShape medicineShape;
    private Double currentPrice;
    private Integer quantity;
    private Double medicineAverageGrade;
    private Long medicineId;
    private String pharmacyName;
    private Long pharmacyId;
    private Integer totalDiscount = 0;

}

package com.mrsisa.pharmacy.dto.stock;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineStockQRSearchDTO {

    Long medicineId;
    Double medicinePrice;
    String medicineName;
    Integer quantity;
    Integer therapyDays;
}

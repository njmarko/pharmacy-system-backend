package com.mrsisa.pharmacy.dto.stock;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineStockReducedInfoDTO {

    Long pharmacyId;
    String pharmacyName;
    Long medicineId;
    Double medicinePrice;


}

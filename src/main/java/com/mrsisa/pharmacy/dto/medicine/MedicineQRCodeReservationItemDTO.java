package com.mrsisa.pharmacy.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineQRCodeReservationItemDTO {

    private Integer quantity;
    private Long medicineId;
    private String medicineName;
    private Integer therapyDays;
}

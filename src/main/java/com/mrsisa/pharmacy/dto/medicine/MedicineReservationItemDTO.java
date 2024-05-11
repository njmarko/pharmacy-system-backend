package com.mrsisa.pharmacy.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineReservationItemDTO {
    private Integer quantity;
    private Long medicineId;
    private String medicineName;
}

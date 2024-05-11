package com.mrsisa.pharmacy.dto.medicine;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReplacementMedicineDTO {
    private Long pharmacyId;
    private Long medicineStockId;
    private Long patientId;
    private Long appointmentId;
    private List<Long> chosenMedicineIds = new ArrayList<>();
    private String medicineName;
}

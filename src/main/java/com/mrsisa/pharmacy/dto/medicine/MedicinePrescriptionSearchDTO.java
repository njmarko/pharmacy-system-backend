package com.mrsisa.pharmacy.dto.medicine;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MedicinePrescriptionSearchDTO {
    private Long pharmacyId;
    private Long patientId;
    private String medicineName = "";
    private List<Long> chosenMedicineIds = new ArrayList<>();
}

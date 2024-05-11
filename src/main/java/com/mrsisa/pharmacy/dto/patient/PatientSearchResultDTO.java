package com.mrsisa.pharmacy.dto.patient;

import com.mrsisa.pharmacy.domain.entities.PatientCategory;
import lombok.Data;

@Data
public class PatientSearchResultDTO {
    private String firstName = "";
    private String lastName = "";
    private PatientCategory patientCategory;
}

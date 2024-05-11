package com.mrsisa.pharmacy.dto.patient;

import lombok.Data;

@Data
public class PatientNotShowedUpDTO {
    private Long patientId;
    private Long appointmentId;
}

package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.dto.patient.PatientDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PatientDTOToPatient extends AbstractConverter<PatientDTO, Patient> implements IConverter<PatientDTO, Patient> {
    @Override
    public Patient convert(@NonNull PatientDTO patientDTO) {
        return getModelMapper().map(patientDTO, Patient.class);
    }
}

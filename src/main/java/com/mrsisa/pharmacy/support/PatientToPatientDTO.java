package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.dto.patient.PatientDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PatientToPatientDTO extends AbstractConverter<Patient, PatientDTO> implements IConverter<Patient, PatientDTO> {
    @Override
    public PatientDTO convert(@NonNull Patient patient) {
        return getModelMapper().map(patient, PatientDTO.class);
    }
}

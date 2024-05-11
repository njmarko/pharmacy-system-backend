package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.dto.patient.PatientSearchResultDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PatientToPatientSearchDTO extends AbstractConverter<Patient, PatientSearchResultDTO> implements IConverter<Patient, PatientSearchResultDTO> {
    @Override
    public PatientSearchResultDTO convert(@NonNull Patient patient) {

        return getModelMapper().map(patient, PatientSearchResultDTO.class);
    }
}

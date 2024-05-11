package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.MissingMedicineLog;
import com.mrsisa.pharmacy.dto.medicine.MissingMedicineDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MissingMedicineLogToMissingMedicineDTO extends AbstractConverter<MissingMedicineLog, MissingMedicineDTO> {
    @Override
    public MissingMedicineDTO convert(@NonNull MissingMedicineLog missingMedicineLog) {
        return getModelMapper().map(missingMedicineLog, MissingMedicineDTO.class);
    }
}

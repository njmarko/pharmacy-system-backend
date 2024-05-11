package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.dto.medicine.MedicineDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MedicineToMedicineDTO extends AbstractConverter<Medicine, MedicineDTO>{
    @Override
    public MedicineDTO convert(@NonNull Medicine medicine) {
        return getModelMapper().map(medicine, MedicineDTO.class);
    }
}

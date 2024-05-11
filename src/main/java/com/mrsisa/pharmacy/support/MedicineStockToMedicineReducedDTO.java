package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.dto.medicine.MedicineReducedDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MedicineStockToMedicineReducedDTO extends AbstractConverter<MedicineStock, MedicineReducedDTO> {
    @Override
    public MedicineReducedDTO convert(@NonNull MedicineStock medicineStock) {
        return getModelMapper().map(medicineStock, MedicineReducedDTO.class);
    }
}

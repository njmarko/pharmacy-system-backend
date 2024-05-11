package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.dto.medicine.MedicineDetailsDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;


@Component
public class MedicineToMedicineDetailsDTO extends AbstractConverter<Medicine, MedicineDetailsDTO>{
    @Override
    public MedicineDetailsDTO convert(@NonNull Medicine medicine) {
        return new MedicineDetailsDTO(medicine.getId(),medicine.getName(), medicine.getMedicineType(), medicine.getManufacturer(),
                medicine.getComposition(), medicine.getMedicineShape(), medicine.getAverageGrade(), medicine.getIssueOnRecipe(),
                medicine.getAdditionalNotes(), medicine.getPoints());
    }
}

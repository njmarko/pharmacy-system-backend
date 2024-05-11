package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.dto.stock.MedicineStockDetailedDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MedicineStockToMedicineStockDetailedDTO extends AbstractConverter<MedicineStock, MedicineStockDetailedDTO> implements IConverter<MedicineStock, MedicineStockDetailedDTO> {
    @Override
    public MedicineStockDetailedDTO convert(@NonNull MedicineStock medicineStock) {
        var medicine = medicineStock.getMedicine();

        return new MedicineStockDetailedDTO(medicineStock.getId(), medicine.getName(), medicine.getMedicineType(),
                medicine.getManufacturer(), medicine.getComposition(), medicine.getMedicineShape(), medicine.getAverageGrade(),
                medicine.getIssueOnRecipe(), medicine.getAdditionalNotes(), medicine.getPoints(), medicineStock.getCurrentPrice(),
                medicineStock.getQuantity());
    }
}

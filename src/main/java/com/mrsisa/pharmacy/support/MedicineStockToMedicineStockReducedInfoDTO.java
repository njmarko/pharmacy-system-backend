package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.dto.stock.MedicineStockReducedInfoDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MedicineStockToMedicineStockReducedInfoDTO extends AbstractConverter<MedicineStock, MedicineStockReducedInfoDTO>{
    @Override
    public MedicineStockReducedInfoDTO convert(@NonNull MedicineStock medicineStock) {
        return new MedicineStockReducedInfoDTO(medicineStock.getPharmacy().getId(),
                medicineStock.getPharmacy().getName(),
                medicineStock.getMedicine().getId(),
                medicineStock.getCurrentPrice());
    }
}

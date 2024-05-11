package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import com.mrsisa.pharmacy.dto.medicine.MedicineOrderInfoDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MedicineOrderInfoToMedicineOrderInfoDTO extends AbstractConverter<MedicineOrderInfo, MedicineOrderInfoDTO> {
    @Override
    public MedicineOrderInfoDTO convert(@NonNull MedicineOrderInfo item) {
        return new MedicineOrderInfoDTO(item.getId(), item.getMedicine().getId(), item.getMedicine().getName(), item.getQuantity(), item.getIsNew(), item.getMedicinePrice());
    }
}

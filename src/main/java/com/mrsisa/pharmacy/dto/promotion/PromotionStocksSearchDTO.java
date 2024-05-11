package com.mrsisa.pharmacy.dto.promotion;

import lombok.Data;

import java.util.List;

@Data
public class PromotionStocksSearchDTO {
    private String name = "";
    private List<Long> medicineIds = null;

    public List<Long> getFixedMedicineIds() {
        if (this.medicineIds == null || this.medicineIds.isEmpty()) {
            return List.of(999999L);
        }
        return this.medicineIds;
    }
}

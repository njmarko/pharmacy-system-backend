package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Promotion;
import com.mrsisa.pharmacy.domain.enums.PromotionStatus;
import com.mrsisa.pharmacy.dto.promotion.PromotionCreationDTO;
import com.mrsisa.pharmacy.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PromotionCreationDTOToPromotion extends AbstractConverter<PromotionCreationDTO, Promotion> {
    private final IMedicineService medicineService;

    @Autowired
    public PromotionCreationDTOToPromotion(IMedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @Override
    public Promotion convert(@NonNull PromotionCreationDTO dto) {
        var promotion = new Promotion(null, dto.getContent(), dto.getFrom(), dto.getTo(), PromotionStatus.ACTIVE);
        dto.getItems().forEach(item -> {
            var medicine = medicineService.get(item.getMedicineId());
            promotion.addItem(medicine, item.getDiscount());
        });
        return promotion;
    }
}

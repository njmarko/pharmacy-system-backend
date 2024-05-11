package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.domain.entities.Promotion;
import com.mrsisa.pharmacy.domain.valueobjects.PromotionItem;

import java.util.List;

public interface IPromotionService extends IJPAService<Promotion> {
    Promotion createPromotion(Long pharmacyId, Promotion promotion);

    void endExpiredPromotions();

    List<PromotionItem> getAllActiveItemsForMedicineStock(MedicineStock medicineStock);

    void applyPromotionDiscounts();
}

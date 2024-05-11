package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.domain.entities.Promotion;
import com.mrsisa.pharmacy.domain.enums.PromotionStatus;
import com.mrsisa.pharmacy.domain.valueobjects.PromotionItem;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.IMedicineStockRepository;
import com.mrsisa.pharmacy.repository.IPharmacyRepository;
import com.mrsisa.pharmacy.repository.IPromotionItemRepository;
import com.mrsisa.pharmacy.repository.IPromotionRepository;
import com.mrsisa.pharmacy.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromotionService extends JPAService<Promotion> implements IPromotionService {
    private final IPromotionRepository promotionRepository;
    private final IPharmacyRepository pharmacyRepository;
    private final IMedicineStockRepository medicineStockRepository;
    private final IPromotionItemRepository promotionItemRepository;

    @Autowired
    public PromotionService(IPromotionRepository promotionRepository, IPharmacyRepository pharmacyRepository, IMedicineStockRepository medicineStockRepository, IPromotionItemRepository promotionItemRepository) {
        this.promotionRepository = promotionRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.medicineStockRepository = medicineStockRepository;
        this.promotionItemRepository = promotionItemRepository;
    }

    @Override
    protected JpaRepository<Promotion, Long> getEntityRepository() {
        return promotionRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {BusinessException.class})
    public Promotion createPromotion(Long pharmacyId, Promotion promotion) {
        var pharmacy = pharmacyRepository.findByIdAndActiveTrue(pharmacyId).orElseThrow(() -> new NotFoundException("Cannot find pharmacy with id: " + pharmacyId));
        promotion.setPharmacy(pharmacy);
        // Check if medicines are present in the current pharmacy and calculate discounts if the promotion starts today
        promotion.getPromotionItems().forEach(promotionItem -> {
            var medicineStock = medicineStockRepository.getMedicineInPharmacy(pharmacyId, promotionItem.getMedicine().getId()).orElseThrow(() -> new BusinessException("Medicine is not registered in the current pharmacy."));
            double newPrice = getNewPrice(medicineStock, promotionItem.getDiscountFactor());
            promotionItem.setPriceReduction(medicineStock.getCurrentPrice() - newPrice);
            if (LocalDate.now().equals(promotion.getFromDate())) {
                medicineStock.setCurrentPrice(newPrice);
            }
        });
        return save(promotion);
    }

    @Override
    public void endExpiredPromotions() {
        promotionRepository.getActiveExpiredPromotionsStream(PromotionStatus.ACTIVE, LocalDate.now()).forEach(promotion -> promotion.getPromotionItems().forEach(item -> {
            // Need to fetch the stock again here so that I can lock it
            medicineStockRepository.getMedicineInPharmacy(promotion.getPharmacy().getId(), item.getMedicine().getId()).ifPresent(medicineStock -> medicineStock.setCurrentPrice(getNewPrice(medicineStock, item.getInverseDiscountFactor())));
            promotion.setPromotionStatus(PromotionStatus.EXPIRED);
        }));
    }

    @Override
    public List<PromotionItem> getAllActiveItemsForMedicineStock(MedicineStock medicineStock) {
        return promotionItemRepository.getItemsWithMedicineInPharmacyList(medicineStock.getPharmacy().getId(), medicineStock.getMedicine().getId(), PromotionStatus.ACTIVE, LocalDate.now());
    }

    @Override
    public void applyPromotionDiscounts() {
        promotionRepository.getPromotionsWhichStartTodayStream(PromotionStatus.ACTIVE, LocalDate.now().plusDays(1)).forEach(promotion -> promotion.getPromotionItems().forEach(item -> {
            // Need to fetch the stock again here so that I can lock it
            medicineStockRepository.getMedicineInPharmacy(promotion.getPharmacy().getId(), item.getMedicine().getId()).ifPresent(medicineStock ->
                    medicineStock.setCurrentPrice(getNewPrice(medicineStock, item.getDiscountFactor())));
            promotion.setPromotionStatus(PromotionStatus.EXPIRED);
        }));
    }

    private Double getNewPrice(MedicineStock stock, Double multiplier) {
        double newPrice = stock.getCurrentPrice() * multiplier;
        return Math.round(newPrice * 100.0) / 100.0;
    }
}

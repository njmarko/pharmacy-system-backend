package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.enums.PromotionStatus;
import com.mrsisa.pharmacy.domain.valueobjects.PromotionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface IPromotionItemRepository extends JpaRepository<PromotionItem, Long> {
    @Query("select item from PromotionItem item where item.promotion.pharmacy.id=:pharmacyId and item.promotion.promotionStatus=:status " +
            "and item.medicine.id=:medicineId and item.promotion.fromDate <= :today and item.promotion.toDate >= :today")
    Stream<PromotionItem> getItemsWithMedicineInPharmacyStream(@Param("pharmacyId") Long pharmacyId,
                                                               @Param("medicineId") Long medicineId,
                                                               @Param("status") PromotionStatus status,
                                                               @Param("today") LocalDate today);

    @Query("select item from PromotionItem item where item.promotion.pharmacy.id=:pharmacyId and item.promotion.promotionStatus=:status" +
            " and item.medicine.id=:medicineId and item.promotion.fromDate <= :today and item.promotion.toDate >= :today")
    List<PromotionItem> getItemsWithMedicineInPharmacyList(@Param("pharmacyId") Long pharmacyId,
                                                               @Param("medicineId") Long medicineId,
                                                               @Param("status") PromotionStatus status,
                                                               @Param("today") LocalDate today);
}

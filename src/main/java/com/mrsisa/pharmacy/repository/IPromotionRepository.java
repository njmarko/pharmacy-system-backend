package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Promotion;
import com.mrsisa.pharmacy.domain.enums.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.stream.Stream;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("select p from Promotion p left join fetch p.promotionItems where p.promotionStatus=:status and p.toDate < :endDate")
    Stream<Promotion> getActiveExpiredPromotionsStream(@Param("status") PromotionStatus status, @Param("endDate") LocalDate endDate);

    @Query("select p from Promotion p left join fetch p.promotionItems where p.promotionStatus=:status and p.fromDate=:today")
    Stream<Promotion> getPromotionsWhichStartTodayStream(@Param("status") PromotionStatus status,
                                                         @Param("today") LocalDate today);
}

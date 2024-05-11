package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.MedicinePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface IMedicinePurchaseRepository extends JpaRepository<MedicinePurchase, Long> {

    @Query("select sum(mp.quantity) from MedicinePurchase mp where mp.active=true and mp.purchasedMedicine.id=:medicineId and mp.pharmacy.id=:pharmacyId" +
            " and mp.datePurchased >= :from and mp.datePurchased < :to")
    Optional<Integer> getMedicinePurchaseCount(@Param("medicineId") Long medicineId,
                                               @Param("pharmacyId") Long pharmacyId,
                                               @Param("from") LocalDate from,
                                               @Param("to") LocalDate to);

    @Query("select sum(mp.price) from MedicinePurchase mp where mp.active=true and mp.pharmacy.id=:pharmacyId and mp.datePurchased >= :from and mp.datePurchased < :to")
    Optional<Double> getIncomeFromMedicines(@Param("pharmacyId") Long pharmacyId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);
}

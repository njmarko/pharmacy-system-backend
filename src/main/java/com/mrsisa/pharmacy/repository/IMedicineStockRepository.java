package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface IMedicineStockRepository extends JpaRepository<MedicineStock, Long> {

    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:id" +
            " and lower(ms.medicine.name) like :name")
    Page<MedicineStock> getMedicineStocksForPharmacy(@Param("id") Long pharmacyId, @Param("name") String medicineName, Pageable pageable);

    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:id and ms.quantity>0")
    Page<MedicineStock> getAvailableMedicineStocksForPharmacy(@Param("id") Long id, Pageable pageable);

    @Query("select ms from MedicineStock ms where ms.medicine.id = :id and ms.active=true and ms.quantity > 0")
    Page<MedicineStock> getAvailableMedicineStocksForMedicine(@Param("id") Long id, Pageable pageable);

    @Query("select ms from MedicineStock ms where ms.medicine.id = :id and ms.quantity >= :quantity and ms.active=true")
    List<MedicineStock> getAllStocksForMedicine(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:id and lower(ms.medicine.code)=:code")
    Optional<MedicineStock> getByMedicineCodeForPharmacy(@Param("code") String medicineCode, @Param("id") Long pharmacyId);

    Optional<MedicineStock> findByIdAndActiveIsTrue(Long id);

    @Query("select ms from MedicineStock ms where ms.id=:id and ms.active=true and ms.pharmacy.id=:pharmacyId")
    Optional<MedicineStock> findByIdInPharmacy(@Param("id") Long stockId, @Param("pharmacyId") Long pharmacyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:pharmacyId and ms.medicine.id=:medicineId")
    Optional<MedicineStock> getMedicineInPharmacy(@Param("pharmacyId") Long pharmacyId,
                                                  @Param("medicineId") Long medicineId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:pharmacyId and ms.id=:stockId")
    Optional<MedicineStock> getMedicineStockInPharmacy(@Param("pharmacyId") Long pharmacyId,
                                                  @Param("stockId") Long stockId);

    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:pharmacyId" +
            " and ms.id not in :chosenMedicineIds and lower(ms.medicine.name) like :name and ms.medicine not in :allergicMedicines")
    Page<MedicineStock> getMedicinesForPharmacyPatientIsNotAllergicTo(@Param("pharmacyId") Long pharmacyId,
                                                                      @Param("name") String medicineName,
                                                                      @Param("chosenMedicineIds") List<Long> chosenMedicineIds,
                                                                      @Param("allergicMedicines") List<Medicine> allergicMedicines,
                                                                      Pageable pageable);

    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:pharmacyId and ms.quantity > 0 and " +
            "ms.medicine.id in :replacementMedicines and ms.id not in :chosenMedicineIds and lower(ms.medicine.name) like :name and ms.medicine not in :allergicMedicines")
    Page<MedicineStock> getReplacementMedicinesForPharmacyPatientIsNotAllergicTo(@Param("pharmacyId") Long pharmacyId,
                                                                                 @Param("allergicMedicines") List<Medicine> allergicMedicines,
                                                                                 @Param("replacementMedicines") List<Long> replacementMedicines,
                                                                                 @Param("name") String medicineName,
                                                                                 @Param("chosenMedicineIds") List<Long> chosenMedicineIds,
                                                                                 Pageable pageable);

    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:id")
    List<MedicineStock> getPharmacyStocksList(@Param("id") Long pharmacyId);

    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:id")
    Stream<MedicineStock> getPharmacyStocksStream(@Param("id") Long pharmacyId);

    @Query("select ms from MedicineStock ms where ms.active=true and ms.pharmacy.id=:pharmacyId" +
            " and ms.medicine.id not in :medicineIds and lower(ms.medicine.name) like concat('%',lower(:name),'%') ")
    Page<MedicineStock> getPharmacyStocksNotInPromotion(@Param("pharmacyId") Long pharmacyId,
                                                        @Param("name") String name,
                                                        @Param("medicineIds") List<Long> medicineIds,
                                                        Pageable pageable);
}

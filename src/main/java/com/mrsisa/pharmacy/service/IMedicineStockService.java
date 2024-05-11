package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMedicineStockService extends IJPAService<MedicineStock> {
    Page<MedicineStock> getMedicineStocksForPharmacy(Pharmacy pharmacy, String medicineName, Pageable pageable);
    Page<MedicineStock> getAvailableMedicinesForPharmacy(Pharmacy pharmacy, Pageable pageable);
    Page<MedicineStock> getAvailableMedicineStocksForMedicine(Long medicineId, Pageable pageable);
    MedicineStock registerMedicineInPharmacy(Long pharmacyId, Medicine medicine, double price, int quantity);
    MedicineStock getMedicineStockForPharmacy(Long pharmacyId, Long id);
    void removeMedicine(Pharmacy pharmacy, MedicineStock medicineStock);
    MedicineStock updateStock(Long pharmacyId, Long stockId, Double newPrice);
    MedicineStock getStockInPharmacy(Long pharmacyId, Long stockId);
    void updatePharmacyStock(Long pharmacyId, Long orderId);
    Boolean isMedicineRegisteredInPharmacy(Pharmacy pharmacy, Medicine medicine);
    Page<MedicineStock> getAllMedicinesForPharmacyAndPatientIsNotAllergicTo(Long pharmacyId, Long patientId,
                                                                            String medicineName, List<Long> chosenMedicineIds,
                                                                            Pageable pageable);
    Page<MedicineStock> getReplacementMedicinesPatientIsNotAllergicTo(Long pharmacyId, Long patientId,
                                                                      Long medicineStockId, String medicineName,
                                                                      List<Long> chosenMedicineIds,
                                                                      Pageable pageable);
    List<MedicineStock> getPharmacyStockList(Pharmacy pharmacy);

    Page<MedicineStock> getPharmacyStocksNotInPromotion(Pharmacy pharmacy, String name, List<Long> medicineIds, Pageable pageable);
}

package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.MissingMedicineLog;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMissingMedicineLogService extends IJPAService<MissingMedicineLog> {
    Page<MissingMedicineLog> getAllForPharmacy(Pharmacy pharmacy, String name, Pageable pageable);
    void insertMissingMedicineLog(Long appointmentId, Long medicineStockId);
}

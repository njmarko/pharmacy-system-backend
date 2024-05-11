package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.MissingMedicineLog;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.repository.IMissingMedicineLogRepository;
import com.mrsisa.pharmacy.service.IAppointmentService;
import com.mrsisa.pharmacy.service.IMedicineStockService;
import com.mrsisa.pharmacy.service.IMissingMedicineLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MissingMedicineLogService extends JPAService<MissingMedicineLog> implements IMissingMedicineLogService {
    private final IMissingMedicineLogRepository missingMedicineLogRepository;
    private final IAppointmentService appointmentService;
    private final IMedicineStockService medicineStockService;

    @Autowired
    public MissingMedicineLogService(IMissingMedicineLogRepository missingMedicineLogRepository,
                                     IAppointmentService appointmentService,
                                     IMedicineStockService medicineStockService) {
        this.missingMedicineLogRepository = missingMedicineLogRepository;
        this.appointmentService = appointmentService;
        this.medicineStockService = medicineStockService;
    }

    @Override
    protected JpaRepository<MissingMedicineLog, Long> getEntityRepository() {
        return missingMedicineLogRepository;
    }

    @Override
    public Page<MissingMedicineLog> getAllForPharmacy(Pharmacy pharmacy, String name, Pageable pageable) {
        return missingMedicineLogRepository.findAllForPharmacy(pharmacy.getId(), name, pageable);
    }

    @Override
    public void insertMissingMedicineLog(Long appointmentId, Long medicineStockId) {
        var appointment = appointmentService.get(appointmentId);
        var medicineStock = medicineStockService.get(medicineStockId);

        this.save(new MissingMedicineLog(LocalDateTime.now(), medicineStock.getMedicine(), appointment));
    }
}

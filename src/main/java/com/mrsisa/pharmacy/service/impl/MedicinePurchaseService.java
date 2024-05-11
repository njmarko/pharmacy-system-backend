package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.MedicinePurchase;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineReservationItem;
import com.mrsisa.pharmacy.repository.IMedicinePurchaseRepository;
import com.mrsisa.pharmacy.service.IMedicinePurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MedicinePurchaseService extends JPAService<MedicinePurchase> implements IMedicinePurchaseService {
    private final IMedicinePurchaseRepository medicinePurchaseRepository;

    @Autowired
    public MedicinePurchaseService(IMedicinePurchaseRepository medicinePurchaseRepository) {
        this.medicinePurchaseRepository = medicinePurchaseRepository;
    }

    @Override
    protected JpaRepository<MedicinePurchase, Long> getEntityRepository() {
        return medicinePurchaseRepository;
    }

    @Override
    public void createMedicinePurchaseFromMedicineReservationItem(MedicineReservationItem medicineReservationItem,
                                                                  Pharmacy pharmacy) {
        var medicinePurchase = new MedicinePurchase(medicineReservationItem.getQuantity(),
                medicineReservationItem.getPrice(), pharmacy, LocalDate.now(), medicineReservationItem.getMedicine());
        medicinePurchaseRepository.save(medicinePurchase);
    }
}

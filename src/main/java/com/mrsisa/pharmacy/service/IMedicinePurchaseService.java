package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.MedicinePurchase;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineReservationItem;

public interface IMedicinePurchaseService extends IJPAService<MedicinePurchase> {
    void createMedicinePurchaseFromMedicineReservationItem(MedicineReservationItem medicineReservationItem,
                                                           Pharmacy pharmacy);
}

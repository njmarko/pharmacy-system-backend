package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IMedicineReservationService extends IJPAService<MedicineReservation> {
    MedicineReservation getMedicineReservationForIssuing(Long id, Long pharmacyId);

    MedicineReservation issueReservation(Long id);

    MedicineReservation makePatientDrugReservation(Long patientId, Long pharmacyId, Long reservedDrugId,
                                                   Integer quantity, LocalDateTime reservedAt,
                                                   LocalDateTime reservationDeadline);

    Page<MedicineReservation> getMedicineReservationsForPatient(Long patientId, Long reservationId, Pageable pageable);

    void cancelReservation(Long reservationId);

    Page<MedicineReservation> getIssuedMedicineReservationsForPatient(Long patientId, Long reservationId, Pageable pageable);

    Review getPatientReviewForDrug(Long patientId, Long medicineId);

    MedicineReservation getPatientReservationById(Long reservationId, Long patientId);
}
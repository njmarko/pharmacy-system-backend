package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.ReservationStatus;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineReservationItem;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.repository.*;
import com.mrsisa.pharmacy.service.IMedicineReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class MedicineReservationService extends JPAService<MedicineReservation> implements IMedicineReservationService {
    private final IMedicineReservationRepository medicineReservationRepository;
    private final IMedicineStockRepository medicineStockRepository;
    private final IPatientRepository patientRepository;
    private final IMedicineRepository medicineRepository;
    private final IPharmacyRepository pharmacyRepository;
    private final IMedicinePurchaseRepository medicinePurchaseRepository;

    @Autowired
    public MedicineReservationService(IMedicineReservationRepository medicineReservationRepository,
                                      IMedicineStockRepository medicineStockRepository, IPatientRepository patientRepository,
                                      IMedicineRepository medicineRepository, IPharmacyRepository pharmacyRepository,
                                      IMedicinePurchaseRepository medicinePurchaseRepository) {
        this.medicineReservationRepository = medicineReservationRepository;
        this.medicineStockRepository = medicineStockRepository;
        this.patientRepository = patientRepository;
        this.medicineRepository = medicineRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.medicinePurchaseRepository = medicinePurchaseRepository;
    }

    @Override
    protected JpaRepository<MedicineReservation, Long> getEntityRepository() {
        return medicineReservationRepository;
    }

    @Override
    public MedicineReservation getMedicineReservationForIssuing(Long id, Long pharmacyId) {
        var medicineReservation = medicineReservationRepository.getMedicineReservationForIssuing(id);
        if (medicineReservation == null || !medicineReservation.getPharmacy().getId().equals(pharmacyId) ||
                medicineReservation.getReservationDeadline().isBefore(LocalDateTime.now())
                || medicineReservation.getReservationStatus() != ReservationStatus.RESERVED) {
            return null;
        }

        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), medicineReservation.getReservationDeadline());
        if (hours > 24) {
            return medicineReservation;
        }

        return null;
    }

    @Override
    public MedicineReservation issueReservation(Long id) {
        var medicineReservation = medicineReservationRepository.getMedicineReservationToIssue(id)
                .orElseThrow(() -> new BusinessException("Reservation has already been issued!"));

        if (medicineReservation.getReservationStatus() != ReservationStatus.RESERVED) {
            throw new BusinessException("Reservation has already been issued!");
        }

        var patient = medicineReservation.getPatient();
        medicineReservation.getReservedMedicines().forEach(item ->{
            var medicinePurchase = new MedicinePurchase(item.getQuantity(),
                    item.getPrice(), medicineReservation.getPharmacy(), LocalDate.now(), item.getMedicine());
            medicinePurchaseRepository.save(medicinePurchase);
            patient.addPoints(item.getMedicine().getPoints() * item.getQuantity());
        });

        patientRepository.save(patient);
        medicineReservation.setReservationStatus(ReservationStatus.PICKED);

        return medicineReservation;
    }

    @Override
    @Transactional(rollbackFor = ResponseStatusException.class)
    public MedicineReservation makePatientDrugReservation(Long patientId,
                                                          Long pharmacyId,
                                                          Long reservedDrugId,
                                                          Integer quantity,
                                                          LocalDateTime reservedAt,
                                                          LocalDateTime reservationDeadline) {

        var pharmacy = this.pharmacyRepository.findByIdAndActiveTrueUnlocked(pharmacyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pharmacy must be specified."));

        var patient = this.patientRepository.findActivePatientUnlocked(patientId, true);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient must be specified.");
        }

        Medicine reservedDrug = this.medicineRepository.getByIdAndActiveTrueUnlocked(reservedDrugId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drug must be specified."));

        // I only allow reservations that have deadline at least 24 hours in the future
        if (reservedAt.plusDays(1).isAfter(reservationDeadline)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation pickup date can't be before the current date!");
        }

        var stock = this.medicineStockRepository.getMedicineInPharmacy(
                pharmacy.getId(), reservedDrug.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drug " +
                        reservedDrug.getName() + " is no longer in the stock!"));

        if (quantity > stock.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Reservation can't be completed because there are not enough drugs left in stock");
        }
        // Set new stock quantity for the pharmacy
        stock.setQuantity(stock.getQuantity() - quantity);

        double discount = (100 - patient.getPatientCategory().getDiscount()) / 100.0;
        double stockItemPrice = Math.round(stock.getCurrentPrice() * discount * 100.0) / 100.0;
        Double price = stockItemPrice * quantity;

        var newReservation = new MedicineReservation(
                price, reservedAt, reservationDeadline, ReservationStatus.RESERVED, pharmacy, patient);
        newReservation.getReservedMedicines()
                .add(new MedicineReservationItem(newReservation, quantity, reservedDrug, price));

        return this.medicineReservationRepository.save(newReservation);

    }

    @Override
    public Page<MedicineReservation> getMedicineReservationsForPatient(Long patientId, Long reservationId, Pageable pageable) {
        return this.medicineReservationRepository.getMedicineReservationsForPatient(patientId, reservationId,
                ReservationStatus.RESERVED, LocalDateTime.now().plusDays(1), pageable);
    }

    @Override
    public Page<MedicineReservation> getIssuedMedicineReservationsForPatient(Long patientId, Long reservationId, Pageable pageable) {
        return this.medicineReservationRepository.getIssuedMedicineReservationsForPatient(patientId, reservationId,
                ReservationStatus.PICKED, pageable);
    }

    @Override
    public Review getPatientReviewForDrug(Long patientId, Long drugId) {
        var patient = this.patientRepository.findActivePatient(patientId, Boolean.TRUE);
        if (patient == null) {
            return null;
        }
        var drug = this.medicineRepository.getByIdAndActiveTrue(drugId)
                .orElse(null);
        if (drug == null) {
            return null;
        }

        // in case user has already reviewed the drug
        return drug.getReviews().stream().filter(r -> r.getReviewer().getId().equals(patient.getId())).findFirst()
                .orElse(null);
    }

    @Override
    public MedicineReservation getPatientReservationById(Long reservationId, Long patientId) {
        var mr = this.medicineReservationRepository.getReservationByIdAndActiveTrue(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment doesn't exist!"));
        if (!mr.getPatient().getId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation doesn't belong to the patient");
        }
        return mr;
    }

    @Override
    @Transactional(rollbackFor = ResponseStatusException.class)
    public void cancelReservation(Long reservationId) {
        var reservation = this.medicineReservationRepository.getMedicineReservationForIssuing(reservationId);
        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Specified Medicine reservation doesn't exist!");
        }
        if (reservation.getReservationStatus() != ReservationStatus.RESERVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can only cancel drug reservations that were not already canceled or picked");
        }

        if (LocalDateTime.now().plusDays(1).isAfter(reservation.getReservationDeadline())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can only cancel drug reservations that are not due in less than 24h.");
        }

        // Restore quantity for pharmacy medicine stock
        for (MedicineReservationItem mri :
                reservation.getReservedMedicines()) {

            Optional<MedicineStock> stock = this.medicineStockRepository.getByMedicineCodeForPharmacy(
                    mri.getMedicine().getCode().toLowerCase(), reservation.getPharmacy().getId());

            stock.ifPresent(medicineStock -> medicineStock.setQuantity(medicineStock.getQuantity() + mri.getQuantity()));

            this.medicineStockRepository.save(stock.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Medicine stock no longer exists!")));
        }
        reservation.setReservationStatus(ReservationStatus.CANCELED);
    }
}

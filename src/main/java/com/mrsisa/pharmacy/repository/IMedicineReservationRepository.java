package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.MedicineReservation;
import com.mrsisa.pharmacy.domain.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface IMedicineReservationRepository extends JpaRepository<MedicineReservation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select mr from MedicineReservation mr join fetch mr.reservedMedicines where mr.id=:id and mr.active=true")
    MedicineReservation getMedicineReservationForIssuing(@Param("id") Long id);


    @Query(value = "select mr from MedicineReservation as mr join fetch mr.reservedMedicines " +
            "where mr.patient.id=:patientId " +
            "and (mr.id=:reservationId or :reservationId is null) " +
            "and mr.reservationStatus=:reservationStatus and mr.active=true and mr.reservationDeadline > :tomorrow",
            countQuery = "select count(mr) from MedicineReservation as mr where mr.patient.id=:patientId " +
                    "and (mr.id=:reservationId or :reservationId is null) " +
                    "and mr.reservationStatus=:reservationStatus and mr.active=true and mr.reservationDeadline > :tomorrow")
    Page<MedicineReservation> getMedicineReservationsForPatient(@Param("patientId") Long patientId,
                                                                @Param("reservationId") Long reservationId,
                                                                @Param("reservationStatus") ReservationStatus reservationStatus,
                                                                @Param("tomorrow") LocalDateTime tomorrow,
                                                                Pageable pageable);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select mr from MedicineReservation mr where mr.id=:id and mr.active=true and mr.reservationStatus=0")
    Optional<MedicineReservation> getMedicineReservationToIssue(@Param("id") Long id);

    @Query(value = "select mr from MedicineReservation as mr " +
            " where mr.active = true " +
            " and mr.reservationStatus = :reservationStatus " +
            " and mr.reservationDeadline < :specifiedDateTime")
    Set<MedicineReservation> getMedicineReservationsBeforeDateTime(@Param("reservationStatus") ReservationStatus reservationStatus,
                                                                   @Param("specifiedDateTime") LocalDateTime specifiedDateTime);

    @Query(value = "select mr from MedicineReservation as mr join fetch mr.reservedMedicines " +
            "where mr.patient.id=:patientId " +
            "and (mr.id=:reservationId or :reservationId is null) " +
            "and mr.reservationStatus=:reservationStatus and mr.active=true ",
            countQuery = "select count(mr) from MedicineReservation as mr where mr.patient.id=:patientId " +
                    "and (mr.id=:reservationId or :reservationId is null) " +
                    "and mr.reservationStatus=:reservationStatus and mr.active=true ")
    Page<MedicineReservation> getIssuedMedicineReservationsForPatient(@Param("patientId") Long patientId,
                                                                      @Param("reservationId") Long reservationId,
                                                                      @Param("reservationStatus") ReservationStatus reservationStatus,
                                                                      Pageable pageable);

    @Query("select distinct mr from MedicineReservation mr join mr.reservedMedicines item where mr.active=true and mr.reservationStatus=0" +
            " and mr.pharmacy.id=:pharmacyId and mr.reservationDeadline > :tomorrow and item.medicine.id=:medicineId")
    Stream<MedicineReservation> getFutureReservationsForPharmacyWithMedicine(@Param("pharmacyId") Long pharmacyId,
                                                                             @Param("medicineId") Long medicineId,
                                                                             @Param("tomorrow") LocalDateTime tomorrow);

    @Query("select count(mr) from MedicineReservation as mr " +
            "where mr.active = true " +
            "and mr.reservationStatus = :reservationStatus " +
            "and mr.pharmacy.id = :pharmacyId " +
            "and mr.patient.id = :patientId")
    Long checkIfPatientHasMedicineReservationsInPharmacy(@Param("patientId") Long patientId,
                                                         @Param("pharmacyId") Long pharmacyId,
                                                         @Param("reservationStatus") ReservationStatus reservationStatus);

    @Query("select count(mr) from MedicineReservation as mr join mr.reservedMedicines as item " +
            "where mr.active = true " +
            "and mr.reservationStatus = :reservationStatus " +
            "and item.medicine.id = :drugId " +
            "and mr.patient.id = :patientId")
    Long checkIfPatientHasMedicineReservationsWithSpecificDrug(@Param("patientId") Long patientId,
                                                               @Param("drugId") Long drugId,
                                                               @Param("reservationStatus") ReservationStatus reservationStatus);

    @Query("select mr from MedicineReservation as mr join fetch mr.reservedMedicines " +
            "where mr.active = true " +
            "and mr.id = :reservationId")
    Optional<MedicineReservation> getReservationByIdAndActiveTrue(@Param("reservationId") Long reservationId);
}

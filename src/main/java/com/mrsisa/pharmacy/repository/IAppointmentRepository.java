package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query(value = "select a from Appointment a where a.employee.pharmacy.id=:id and a.employee.pharmacyEmployee.employeeType=1" +
            " and a.active=true and a.appointmentStatus=3 and a.from >= current_timestamp " +
            " and a.from >= :fromTime and a.to <= :toTime")
    List<Appointment> getAvailableDermatologistAppointmentsForPharmacy(@Param("id") Long pharmacyId,
                                                                       @Param("fromTime") LocalDateTime fromTime,
                                                                       @Param("toTime") LocalDateTime toTime);

    @Query(value = "select a from Appointment a where a.patient.id=:id " +
            " and a.employee.pharmacyEmployee.employeeType = :employeeType" +
            " and a.active=true and a.appointmentStatus=0 and a.from >= current_timestamp " +
            " and a.from >= :fromTime and a.to <= :toTime")
    List<Appointment> getScheduledAppointmentsForPatientCalendar(@Param("id") Long pharmacyId,
                                                                 @Param("employeeType") EmployeeType employeeType,
                                                                 @Param("fromTime") LocalDateTime fromTime,
                                                                 @Param("toTime") LocalDateTime toTime);


    @Query("select a from Appointment a where a.active=true and a.employee.active=true and a.employee.id=:contractId" +
            " and a.appointmentStatus=0 and a.from >= current_timestamp ")
    Set<Appointment> getBookedAppointmentsForContract(@Param("contractId") Long contractId);

    //Replaced pessimistic lock with optimistic lock
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Appointment> getAppointmentByIdAndActiveTrue(@Param("id") Long id);


    // For some reason != is written as <>
    // https://stackoverflow.com/a/24989843
    @Query("select a from Appointment a where a.active=true and a.employee.id=:id" +
            " and ((a.from <= :toTime) and (:fromTime <= a.to)) and a.appointmentStatus <> 1")
    Optional<Appointment> getAppointmentForContractInTime(@Param("id") Long contractId,
                                                          @Param("fromTime") LocalDateTime fromTime,
                                                          @Param("toTime") LocalDateTime toTime);


    @Query(value = "select a from Appointment as a where a.active=true and a.patient.id=:id and (a.appointmentStatus=:status or :status is null) " +
            "and ((:from >= a.from and :from < a.to) or (:to > a.from and :to<= a.to) or (:from <= a.from and :to >= a.to))")
    List<Appointment> findOverlappingAppointments(@Param("id") Long id, @Param("status") AppointmentStatus status,
                                                  @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "select a from Appointment as a where a.active=true and a.patient.id=:id " +
            "and (a.appointmentStatus=:status or :status is null)" +
            "and (a.employee.pharmacyEmployee.employeeType = :employeeType)" +
            "and ( (lower(a.employee.pharmacy.name) like concat('%',:name,'%') or :name is null) " +
            "or (lower(a.employee.pharmacyEmployee.firstName) like concat('%',:name,'%') or :name is null)" +
            "or (lower(a.employee.pharmacyEmployee.lastName) like concat('%',:name,'%') or :name is null) )" +
            "and a.from >= :currentTime")
    Page<Appointment> getScheduledAppointmentsForPatient(@Param("id") Long id,
                                                         @Param("employeeType") EmployeeType employeeType,
                                                         @Param("name") String name,
                                                         @Param("status") AppointmentStatus booked,
                                                         @Param("currentTime") LocalDateTime currentTime,
                                                         Pageable pageable);

    @Query("select a from Appointment a where a.active=true and a.employee.id=:id" +
            " and ((a.from <= :toTime) and (:fromTime <= a.to)) and a.appointmentStatus <> 2")
    List<Appointment> getAppointmentsForEmployee(@Param("id") Long contractId,
                                                 @Param("fromTime") LocalDateTime fromTime,
                                                 @Param("toTime") LocalDateTime toTime);

    @Query("select a from Appointment a where a.active=true and a.employee.pharmacyEmployee.id=:id" +
            " and ((a.from <= :toTime) and (:fromTime <= a.to)) and a.appointmentStatus <> 2")
    Stream<Appointment> getAppointmentsForEmployeePerson(@Param("id") Long employeeId,
                                                         @Param("fromTime") LocalDateTime fromTime,
                                                         @Param("toTime") LocalDateTime toTime);

    @Query("select a from Appointment a where a.active=true and a.employee.pharmacy.id=:id and " +
            "a.appointmentStatus=3 and a.from >= current_timestamp and a.employee.pharmacyEmployee.employeeType=1")
    Page<Appointment> getDermatologistAppointments(@Param("id") Long pharmacyId, Pageable pageable);

    @Query(value = "select a from Appointment as a where a.active=true and a.patient.id=:id " +
            "and (a.appointmentStatus=:status or :status is null)" +
            "and (a.employee.pharmacyEmployee.employeeType = :employeeType)" +
            "and ( (lower(a.employee.pharmacy.name) like concat('%',:name,'%') or :name is null) " +
            "or (lower(a.employee.pharmacyEmployee.firstName) like concat('%',:name,'%') or :name is null)" +
            "or (lower(a.employee.pharmacyEmployee.lastName) like concat('%',:name,'%') or :name is null) )")
    Page<Appointment> getPreviousAppointmentsForPatient(@Param("id") Long id,
                                                        @Param("employeeType") EmployeeType employeeType,
                                                        @Param("name") String name,
                                                        @Param("status") AppointmentStatus tookPlace,
                                                        Pageable pageable);

    @Query(value = "select a from Appointment as a where a.active=true and a.employee.pharmacy.id=:pharmacyId " +
            "and a.appointmentStatus=:status " +
            "and (a.employee.pharmacyEmployee.employeeType = :employeeType) " +
            "and :dateTime between a.from and a.to " +
            "and ( (lower(a.employee.pharmacy.name) like concat('%',:name,'%') or :name is null) " +
            "or (lower(a.employee.pharmacyEmployee.firstName) like concat('%',:name,'%') or :name is null) " +
            "or (lower(a.employee.pharmacyEmployee.lastName) like concat('%',:name,'%') or :name is null) )")
    Page<Appointment> getAvailablePharmacistAppointmentsForPharmacyOnSpecifiedDateAndTime(@Param("pharmacyId") Long pharmacyId,
                                                                                          @Param("employeeType") EmployeeType employeeType,
                                                                                          @Param("name") String name,
                                                                                          @Param("dateTime") LocalDateTime dateTime,
                                                                                          @Param("status") AppointmentStatus appointmentStatus,
                                                                                          Pageable pageable);

    @Query("select a from Appointment a where a.active=true and a.employee.pharmacyEmployee.id=:id" +
            " and ((a.from <= :currTime) and (:currTime <= a.to)) and a.appointmentStatus = 0")
    Optional<Appointment> getAppointmentInProgressForEmployee(@Param("id") Long employeeId,
                                                              @Param("currTime") LocalDateTime currTime);

    @Query("select count(a) from Appointment " +
            "a where a.active=true " +
            "and a.employee.pharmacyEmployee.id=:employeeId " +
            "and a.patient.id = :patientId " +
            "and a.appointmentStatus = :appointmentStatus")
    Long checkIfPatientHasAppointmentWithEmployee(@Param("patientId") Long patientId,
                                                  @Param("employeeId") Long employeeId,
                                                  @Param("appointmentStatus") AppointmentStatus appointmentStatus);


    @Query("select count(a) from Appointment " +
            "a where a.active=true " +
            "and a.employee.pharmacy.id=:pharmacyId " +
            "and a.patient.id = :patientId " +
            "and a.appointmentStatus = :appointmentStatus")
    Long checkIfPatientHadAppointmentWithEmployeeFromPharmacy(@Param("patientId") Long patientId,
                                                              @Param("pharmacyId") Long pharmacyId,
                                                              @Param("appointmentStatus") AppointmentStatus appointmentStatus);


    @Query("select count(a) from Appointment a where a.appointmentStatus=:status and a.employee.pharmacy.id=:pharmacyId and a.employee.pharmacyEmployee.employeeType=:employeeType" +
            " and a.from >= :from and a.to < :to")
    Optional<Long> countAppointmentsForPharmacy(@Param("pharmacyId") Long pharmacyId,
                                                @Param("status") AppointmentStatus status,
                                                @Param("employeeType") EmployeeType employeeType,
                                                @Param("from") LocalDateTime from,
                                                @Param("to") LocalDateTime to);

    @Query("select sum(a.price) from Appointment a where a.employee.pharmacy.id=:pharmacyId and a.employee.pharmacyEmployee.employeeType=:type and a.appointmentStatus=:status" +
            " and a.from >= :from and a.to < :to")
    Optional<Double> getAppointmentsIncome(@Param("pharmacyId") Long pharmacyId,
                                           @Param("status") AppointmentStatus status,
                                           @Param("type") EmployeeType type,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);

    @Query("select a from Appointment a where a.active=true and a.employee.id=:employeeId" +
            " and ((a.from <= :nowDate) and (:nowDate <= a.to)) and a.appointmentStatus = 0" +
            " and a.patient.id=:patientId")
    Optional<Appointment> getScheduledAppointmentForPatientAndEmployee(@Param("employeeId") Long employeeId,
                                                                       @Param("patientId") Long patientId,
                                                                       @Param("nowDate") LocalDateTime dateNow);

    @Query("select a from Appointment a where a.active=true and a.employee.pharmacyEmployee.id=:employeeId" +
            " and a.to > :nowDate and (a.appointmentStatus = 0 or a.appointmentStatus = 3)")
    List<Appointment> getAllBusyAppointmentsForEmployee(@Param("employeeId") Long employeeId,
                                                        @Param("nowDate") LocalDateTime dateNow);

    @Query("select a from Appointment a where a.active=true and a.employee.pharmacyEmployee.id=:employeeId" +
            " and a.to >= current_date and a.to >= :from and a.to <= :to and (a.appointmentStatus = 0 or a.appointmentStatus = 3)")
    List<Appointment> getAllBusyAppointmentsForEmployeeForRange(@Param("employeeId") Long employeeId,
                                                                @Param("from") LocalDateTime from,
                                                                @Param("to") LocalDateTime to);

    @Query("select a from Appointment a where a.active=true and a.patient.id=:patientId" +
            " and a.from > :nowDate and a.appointmentStatus = 0 and a.employee.pharmacyEmployee.id<>:employeeId")
    List<Appointment> getAllBookedAppointmentsForPatientWithoutEmployee(@Param("patientId") Long patientId,
                                                         @Param("employeeId") Long employeeId,
                                                         @Param("nowDate") LocalDateTime dateNow);

    @Query("select a from Appointment a where a.active=true and a.patient.id=:patientId" +
            " and a.from > :nowDate and a.appointmentStatus = 0")
    List<Appointment> getAllBookedAppointmentsForPatient(@Param("patientId") Long patientId,
                                                         @Param("nowDate") LocalDateTime dateNow);

    @Query("select a from Appointment a where a.active=true and a.employee.id=:employeeId" +
            " and a.from >= :from and a.to <= :to and a.appointmentStatus = 3")
    List<Appointment> getAllAvailableDermatologistAppointmentsForDateRange(@Param("employeeId") Long employeeId,
                                                                           @Param("from") LocalDateTime from,
                                                                           @Param("to") LocalDateTime to);

    @Query("select a from Appointment a where a.active=true and a.patient.id=:patientId" +
            " and a.from >= :from and a.to <= :to and a.appointmentStatus = 0")
    List<Appointment> getAllBookedAppointmentsForPatientForRange(@Param("patientId") Long patientId,
                                                                 @Param("from") LocalDateTime from,
                                                                 @Param("to") LocalDateTime to);

    @Query("select a from Appointment a where a.active=true and a.appointmentStatus = 1" +
            " and (cast(:from as date) is null or a.from >= :from ) and (cast(:to as date) is null or a.to <= :to) and a.employee.pharmacyEmployee.id = :id" +
            " and lower(a.patient.firstName) like :firstName and lower(a.patient.lastName) like :lastName")
    Page<Appointment> getSearchAndFilterExaminedPatients(@Param("firstName") String firstName,
                                                         @Param("lastName") String lastName,
                                                         @Param("from") LocalDateTime from,
                                                         @Param("to") LocalDateTime to,
                                                         @Param("id") Long employeeId,
                                                         Pageable pageable);
}

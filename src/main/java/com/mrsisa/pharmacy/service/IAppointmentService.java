package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.dto.stock.MedicineStockConcludeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService extends IJPAService<Appointment> {

    List<Appointment> getAvailableDermatologistAppointmentsForPharmacy(Pharmacy pharmacy, LocalDateTime fromTime,
                                                                       LocalDateTime toTime);

    Appointment bookDermatologistAppointment(Long id, Long appointmentId);

    Appointment bookPharmacistAppointment(Long id, Long appointmentId);


    void cancelAppointment(Long id, Long appointmentId);

    Page<Appointment> getScheduledAppointments(Long id, EmployeeType employeeType, String name, Pageable pageable);

    List<Appointment> getAppointmentsForEmployee(Long pharmacyId, Long employeeId, LocalDateTime fromTime,
                                                 LocalDateTime toTime, EmployeeType employeeType);

    Page<Appointment> getAvailableDermatologistAppointmentsForPharmacy(Pharmacy pharmacy, Pageable pageable);

    List<Appointment> getScheduledAppointmentsForPatient(Patient patient, EmployeeType employeeType,
                                                         LocalDateTime parse, LocalDateTime parse1);

    Page<Appointment> getPreviousAppointmentsForPatient(Long id, EmployeeType employeeType, String name,
                                                        Pageable pageable);

    Appointment getAppointmentInProgressForEmployee(Long employeeId);

    void checkIfFillingThatPatientHasNotShowedUpIsValid(Long appointmentId, Long patientId, Long pharmacyEmployeeId);

    Page<Appointment> getAvailablePharmacistAppointmentsForPharmacyOnSpecifiedDateAndTime(Long pharmacyId, String name,
                                                                                          String dateTime,
                                                                                          Pageable pageable);

    void concludeAppointment(String reportText, Long pharmacyId, Long patientId,
                             Long appointmentId, List<MedicineStockConcludeDTO> medicineStockConcludeDTOS);

    Appointment getPatientAppointmentById(Long appointmentId, Long patientId);

    Appointment getScheduledAppointmentForPatientAndEmployee(Long employeeId, Long patientId);

    void scheduleAvailableDermatologistAppointment(Long appointmentId, Long patientId);

    List<Appointment> getAllBusyAppointmentsForEmployee(Long employeeId);

    List<Appointment> getAllBookedAppointmentsForPatientNotWithEmployee(Long patientId, Long employeeId);

    Appointment scheduleAppointmentForEmployee(LocalDateTime from, LocalDateTime to, Long patientId, EmploymentContract employmentContract,
                                        Long pharmacyId);

    Page<Appointment> getSearchAndFilterExaminedPatients(String firstName, String lastName, LocalDateTime from,
                                                         LocalDateTime to, Pageable pageable, Long employeeId);

    List<Appointment> getAllAvailableDermatologistAppointments(LocalDateTime fromTime, LocalDateTime toTime, Long employeeId,
                                                               Long patientId);

    List<Appointment> getAllBookedAppointmentsForPatient(Long patientId);

    List<Appointment> getAllBusyAppointmentsForEmployeeForRange(Long employeeId, LocalDateTime from, LocalDateTime to);

    Appointment createAvailableAppointment(Long pharmacyId, Long employeeId, LocalDateTime fromTime, LocalDateTime toTime);
}

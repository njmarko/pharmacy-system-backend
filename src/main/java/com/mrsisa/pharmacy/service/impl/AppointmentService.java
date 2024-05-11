package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.enums.LeaveDaysRequestStatus;
import com.mrsisa.pharmacy.domain.enums.ReservationStatus;
import com.mrsisa.pharmacy.domain.valueobjects.*;
import com.mrsisa.pharmacy.dto.stock.MedicineStockConcludeDTO;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.*;
import com.mrsisa.pharmacy.service.*;
import com.mrsisa.pharmacy.util.AppointmentOverlapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService extends JPAService<Appointment> implements IAppointmentService {
    private final IAppointmentRepository appointmentRepository;
    private final IPatientRepository patientRepository;
    private final IPharmacyRepository pharmacyRepository;
    private final IEmploymentContractRepository employmentContractRepository;
    private final ILeaveDaysRequestRepository leaveDaysRequestRepository;
    private final IMedicineReservationRepository medicineReservationRepository;
    private final IMedicineStockRepository medicineStockRepository;
    private final IRecipeRepository recipeRepository;
    private final ISystemSettingsRepository systemSettingsRepository;
    private final IEmailService emailService;
    private final IPatientService patientService;

    private static final String NO_PATIENT = "Patient doesn't exist!";
    private static final String NO_APPOINTMENT = "Appointment doesn't exist!";
    private static final String PATIENT_WITH_ID = "Patient with id  ";
    private static final String APPOINTMENT_WITH_ID = "Appointment with id ";
    private static final String DOES_NOT_EXIST = " does not exist";

    @Autowired
    public AppointmentService(IAppointmentRepository appointmentRepository, IPatientRepository patientRepository,
                              IPharmacyRepository pharmacyRepository,
                              IEmploymentContractRepository employmentContractRepository,
                              ILeaveDaysRequestRepository leaveDaysRequestRepository,
                              IMedicineReservationRepository medicineReservationRepository,
                              IMedicineStockRepository medicineStockRepository,
                              IRecipeRepository recipeRepository,
                              IEmailService emailService, ISystemSettingsRepository systemSettingsRepository, IPatientService patientService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.employmentContractRepository = employmentContractRepository;
        this.leaveDaysRequestRepository = leaveDaysRequestRepository;
        this.medicineReservationRepository = medicineReservationRepository;
        this.medicineStockRepository = medicineStockRepository;
        this.recipeRepository = recipeRepository;
        this.emailService = emailService;
        this.systemSettingsRepository = systemSettingsRepository;
        this.patientService = patientService;
    }

    @Override
    protected JpaRepository<Appointment, Long> getEntityRepository() {
        return appointmentRepository;
    }

    @Override
    public List<Appointment> getAvailableDermatologistAppointmentsForPharmacy(Pharmacy pharmacy, LocalDateTime fromTime, LocalDateTime toTime) {
        return appointmentRepository.getAvailableDermatologistAppointmentsForPharmacy(pharmacy.getId(), fromTime, toTime);
    }

    @Override
    @Transactional(rollbackFor = ResponseStatusException.class)
    public Appointment bookDermatologistAppointment(Long id, Long appointmentId) {
        var patient = this.patientRepository.findActivePatientUnlocked(id, true);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NO_PATIENT);
        }
        var appointment = this.get(appointmentId);

        if (!appointment.getEmployee().getPharmacyEmployee().getEmployeeType().equals(EmployeeType.DERMATOLOGIST)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is not a dermatologist appointment.");
        }

        if (patient.getMyAppointments().contains(appointment)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient already booked that appointment.");
        }

        if (!appointment.getAppointmentStatus().equals(AppointmentStatus.AVAILABLE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Appointment is not available and can't be booked.");
        }

        // I look when to appointment start here. If it starts before the current date and time it can't be booked
        if (appointment.getFrom().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This appointments start date and time has passed and can't be booked.");
        }

        //check for overlapping between this appointments timeslot and existing booked appointments
        if (!this.appointmentRepository.findOverlappingAppointments(patient.getId(),
                AppointmentStatus.BOOKED, appointment.getFrom(), appointment.getTo()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You already have an appointment scheduled at this time interval.");
        }

        appointment.setAppointmentStatus(AppointmentStatus.BOOKED);
        appointment.setPatient(patient);

        return appointment;
    }

    @Override
    public void scheduleAvailableDermatologistAppointment(Long appointmentId, Long patientId) {
        var appointment = this.get(appointmentId);
        var patient = this.patientRepository.findById(patientId).orElseThrow(
                () -> new BusinessException(PATIENT_WITH_ID + patientId + DOES_NOT_EXIST));

        appointment.setPatient(patient);
        appointment.setAppointmentStatus(AppointmentStatus.BOOKED);
    }

    @Override
    @Transactional(rollbackFor = ResponseStatusException.class)
    public Appointment bookPharmacistAppointment(Long id, Long appointmentId) {
        var patient = this.patientRepository.findActivePatientUnlocked(id, true);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NO_PATIENT);
        }
        var appointment = this.appointmentRepository.getAppointmentByIdAndActiveTrue(appointmentId)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, NO_APPOINTMENT);
                });

        if (!appointment.getEmployee().getPharmacyEmployee().getEmployeeType().equals(EmployeeType.PHARMACIST)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is not a dermatologist appointment.");
        }

        if (patient.getMyAppointments().contains(appointment)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient already booked that appointment.");
        }

        if (!appointment.getAppointmentStatus().equals(AppointmentStatus.AVAILABLE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Appointment is not available and can't be booked.");
        }

        // I look when to appointment start here. If it starts before the current date and time it can't be booked
        if (appointment.getFrom().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This appointments start date and time has passed and can't be booked.");
        }

        //check for overlapping between this appointments timeslot and existing booked appointments
        if (!this.appointmentRepository.findOverlappingAppointments(patient.getId(),
                AppointmentStatus.BOOKED, appointment.getFrom(), appointment.getTo()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You already have an appointment scheduled at this time interval.");
        }

        appointment.setAppointmentStatus(AppointmentStatus.BOOKED);
        appointment.setPatient(patient);

        return appointment;
    }

    @Override
    public Appointment createAvailableAppointment(Long pharmacyId, Long employeeId, LocalDateTime fromTime, LocalDateTime toTime) {
        var pharmacy = pharmacyRepository.findByIdAndActiveTrue(pharmacyId).orElseThrow(() -> new NotFoundException("Cannot find pharmacy with id: " + pharmacyId));
        // Check if the employee works in the selected pharmacy
        var contract = employmentContractRepository.getEmployeeContractWithPharmacyForUpdate(employeeId, pharmacyId).orElseThrow(() -> new BusinessException("Employee does not work in the selected pharmacy."));
        // Check if the employee works at that time in the selected pharmacy
        throwIfEmployeeDoesNotWork(contract, fromTime, toTime);
        // Check if the employee already has appointments created at that time
        appointmentRepository.getAppointmentForContractInTime(contract.getId(), fromTime, toTime).ifPresent(appointment -> {
            throw new BusinessException("Employee already has booked appointment at that time.");
        });
        // Check if the employee has pending leave requests
        leaveDaysRequestRepository.findForEmployeeContainingDate(contract.getPharmacyEmployee().getId(), fromTime.toLocalDate(), LeaveDaysRequestStatus.PENDING).findAny().ifPresent(req -> {
            throw new BusinessException("Employee has pending leave days request at the selected time.");
        });
        // Check if the employee is on leave
        leaveDaysRequestRepository.findForEmployeeContainingDate(contract.getPharmacyEmployee().getId(), fromTime.toLocalDate(), LeaveDaysRequestStatus.APPROVED).findAny().ifPresent(leaveDaysRequest -> {
            throw new BusinessException("Employee is on leave on the selected date.");
        });
        var appointment = new Appointment(fromTime, toTime, pharmacy.getAppointmentPrice(contract.getPharmacyEmployee().getEmployeeType()), AppointmentStatus.AVAILABLE, contract);
        contract.getBookedAppointments().add(appointment);
        return save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsForEmployee(Long pharmacyId, Long employeeId, LocalDateTime fromTime, LocalDateTime toTime, EmployeeType employeeType) {
        Optional<EmploymentContract> contract = employmentContractRepository.getEmployeeContractWithPharmacy(employeeId, pharmacyId, employeeType);
        List<Appointment> newAppointments = new ArrayList<>();

        if (contract.isPresent()) {
            List<Appointment> appointments = appointmentRepository.getAppointmentsForEmployee(contract.get().getId(), fromTime, toTime);
            var thisTime = LocalDateTime.now();

            for (Appointment appointment : appointments) {
                if ((appointment.getAppointmentStatus() == AppointmentStatus.AVAILABLE && appointment.getFrom().isAfter(thisTime)) ||
                        (appointment.getAppointmentStatus() == AppointmentStatus.BOOKED && appointment.getTo().isAfter(thisTime)) ||
                        appointment.getAppointmentStatus() == AppointmentStatus.TOOK_PLACE) {
                    newAppointments.add(appointment);
                }
            }
        }

        return newAppointments;
    }

    @Override
    public Page<Appointment> getAvailableDermatologistAppointmentsForPharmacy(Pharmacy pharmacy, Pageable pageable) {
        return appointmentRepository.getDermatologistAppointments(pharmacy.getId(), pageable);
    }

    @Override
    public List<Appointment> getScheduledAppointmentsForPatient(Patient patient, EmployeeType employeeType,
                                                                LocalDateTime fromTime, LocalDateTime toTime) {
        return appointmentRepository.getScheduledAppointmentsForPatientCalendar(patient.getId(), employeeType,
                fromTime, toTime);
    }

    private void throwIfEmployeeDoesNotWork(EmploymentContract contract, LocalDateTime from, LocalDateTime to) {
        var dayOfWeek = from.getDayOfWeek();
        var workingDay = contract.getWorkingHours().stream().filter(wd -> wd.getDay().equals(dayOfWeek)).findAny().orElseThrow(() -> new BusinessException("Employee does not work on " + dayOfWeek.toString()));
        if (!(beforeOrEqual(workingDay.getFromHours(), from.toLocalTime()) && beforeOrEqual(to.toLocalTime(), workingDay.getToHours()))) {
            throw new BusinessException("Employee does not work in a given time period.");
        }
    }

    private boolean beforeOrEqual(LocalTime x, LocalTime y) {
        return x.isBefore(y) || x.equals(y);
    }

    public Page<Appointment> getPreviousAppointmentsForPatient(Long id, EmployeeType employeeType, String name, Pageable pageable) {
        var patient = this.patientRepository.findActivePatient(id, true);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NO_PATIENT);
        }
        if (name != null){
            name = name.toLowerCase();
        }

        return this.appointmentRepository.getPreviousAppointmentsForPatient(id, employeeType, name,
                AppointmentStatus.TOOK_PLACE, pageable);
    }

    @Override
    public Page<Appointment> getAvailablePharmacistAppointmentsForPharmacyOnSpecifiedDateAndTime(Long pharmacyId,
                                                                                                 String name,
                                                                                                 String dateTime,
                                                                                                 Pageable pageable) {
        this.pharmacyRepository.findByIdAndActiveTrue(pharmacyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacy doesn't exist!"));
        LocalDateTime dateTimeParam;
        try {
            dateTimeParam = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date and time for the pharmacist appointment must be specified.");
        }
        return this.appointmentRepository.getAvailablePharmacistAppointmentsForPharmacyOnSpecifiedDateAndTime(pharmacyId,
                EmployeeType.PHARMACIST, name, dateTimeParam, AppointmentStatus.AVAILABLE, pageable);
    }

    @Override
    public Appointment getAppointmentInProgressForEmployee(Long employeeId) {
        return appointmentRepository.getAppointmentInProgressForEmployee(employeeId, LocalDateTime.now()).orElseThrow(() -> new BusinessException("No appointments in progress for employee " + employeeId));
    }

    @Override
    public void checkIfFillingThatPatientHasNotShowedUpIsValid(Long appointmentId, Long patientId, Long pharmacyEmployeeId) {
        var appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new BusinessException("Appointment with id  " + appointmentId + DOES_NOT_EXIST));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new BusinessException(PATIENT_WITH_ID + patientId + " not in appointment with id " + appointmentId);
        }

        if (appointment.getFrom().isAfter(LocalDateTime.now())) {
            throw new BusinessException(APPOINTMENT_WITH_ID + appointmentId + " has not started yet");
        }

        if (appointment.getAppointmentStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessException(APPOINTMENT_WITH_ID + appointmentId + " was not booked");
        }

        if (!appointment.getEmployee().getPharmacyEmployee().getId().equals(pharmacyEmployeeId)) {
            throw new BusinessException(APPOINTMENT_WITH_ID + appointmentId + " is not employees appointment with id " + pharmacyEmployeeId);
        }

        appointment.setAppointmentStatus(AppointmentStatus.TOOK_PLACE);
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public void concludeAppointment(String reportText, Long pharmacyId, Long patientId, Long appointmentId, List<MedicineStockConcludeDTO> medicineStockConcludeDTOS) {
        var appointment = this.get(appointmentId);
        var patient = patientRepository.findById(patientId).orElseThrow(() -> new BusinessException(PATIENT_WITH_ID + patientId + DOES_NOT_EXIST));
        var pharmacy = pharmacyRepository.findById(pharmacyId).orElseThrow(() -> new BusinessException("Pharmacy with id  " + pharmacyId + DOES_NOT_EXIST));

        var recipe = new Recipe(LocalDateTime.now(), false, patient, pharmacy);
        var totalPrice = 0.0;

        var medicineReservation = new MedicineReservation(0.0, LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), ReservationStatus.RESERVED, pharmacy,
                appointment.getPatient());

        for(MedicineStockConcludeDTO medicineStockConcludeDTO : medicineStockConcludeDTOS) {
            var medicineStock = medicineStockRepository.getMedicineStockInPharmacy(pharmacyId, medicineStockConcludeDTO.getMedicineStockId())
                    .orElseThrow(() -> new BusinessException("Medicine stock with id " + medicineStockConcludeDTO.getMedicineStockId() + DOES_NOT_EXIST));

            if (medicineStock.getQuantity() < medicineStockConcludeDTO.getQuantity()) {
                throw new BusinessException("Not enough quantity of medicine " + medicineStock.getMedicine().getName() + "!");
            }

            totalPrice += medicineStock.getCurrentPrice() * medicineStockConcludeDTO.getQuantity();

            medicineStock.setQuantity(medicineStock.getQuantity() - medicineStockConcludeDTO.getQuantity());

            var medicineReservationItem = new MedicineReservationItem(medicineReservation, medicineStockConcludeDTO.getQuantity(),
                    medicineStock.getMedicine(), medicineStock.getCurrentPrice());

            var recipeMedicineInfo = new RecipeMedicineInfo(recipe, medicineStockConcludeDTO.getQuantity(),
                    medicineStockConcludeDTO.getTherapyDays(), medicineStock.getMedicine(), medicineStock.getCurrentPrice());

            recipe.getReservedMedicines().add(recipeMedicineInfo);
            medicineReservation.getReservedMedicines().add(medicineReservationItem);
        }

        var report = new Report(reportText);

        if (!medicineStockConcludeDTOS.isEmpty()) {
            medicineReservation.setPrice(totalPrice);
            medicineReservationRepository.save(medicineReservation);
            emailService.sendDrugReservationCreatedMessage(medicineReservation);

            recipe.setPrice(totalPrice);
            recipeRepository.save(recipe);

            report.setRecipe(recipe);
        }

        appointment.setReport(report);
        appointment.setAppointmentStatus(AppointmentStatus.TOOK_PLACE);
        double discount = (100 - patient.getPatientCategory().getDiscount()) / 100.0;
        double price = Math.round(appointment.getPrice() * discount * 100.0) / 100.0;
        appointment.setPrice(price);

        Optional<SystemSettings> optionalSystemSettings = this.systemSettingsRepository.findById(1L);
        if(optionalSystemSettings.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No system settings found. Contact the system administrator.");
        var settings = optionalSystemSettings.get();
        var employee = appointment.getEmployee().getPharmacyEmployee();
        Integer points = employee.getEmployeeType() == EmployeeType.PHARMACIST ? settings.getPharmacistAppointmentPoints() : settings.getDermatologistAppointmentPoints();
        patient.addPoints(points);
        this.patientService.update(patient);
    }

    @Override
    public Appointment getPatientAppointmentById(Long appointmentId, Long patientId) {
        var a = this.appointmentRepository.getAppointmentByIdAndActiveTrue(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NO_APPOINTMENT));
        if (!a.getPatient().getId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment doesn't belong to the patient");
        }
        return a;
    }

    @Override
    public Appointment getScheduledAppointmentForPatientAndEmployee(Long employeeId, Long patientId) {
        return this.appointmentRepository.getScheduledAppointmentForPatientAndEmployee(employeeId, patientId, LocalDateTime.now())
                .orElseThrow(() -> new BusinessException("No appointment in progress now. "));
    }

    @Override
    public List<Appointment> getAllBusyAppointmentsForEmployee(Long employeeId) {
        return this.appointmentRepository.getAllBusyAppointmentsForEmployee(employeeId, LocalDateTime.now());
    }

    @Override
    public List<Appointment> getAllBookedAppointmentsForPatientNotWithEmployee(Long patientId, Long employeeId) {
        return this.appointmentRepository.getAllBookedAppointmentsForPatientWithoutEmployee(patientId, employeeId, LocalDateTime.now());
    }

    @Override
    public Appointment scheduleAppointmentForEmployee(LocalDateTime from, LocalDateTime to, Long patientId, EmploymentContract employmentContract,
                                               Long pharmacyId) {
        var employeeType = employmentContract.getPharmacyEmployee().getEmployeeType();
        Double appointmentPrice;

        var pharmacy = pharmacyRepository.findById(pharmacyId).orElseThrow(
                () -> new BusinessException("Pharmacy with id  " + pharmacyId+ DOES_NOT_EXIST));

        if (employeeType == EmployeeType.DERMATOLOGIST) {
            appointmentPrice = pharmacy.getCurrentDermatologistAppointmentPrice();
        }
        else {
            appointmentPrice = pharmacy.getCurrentPharmacistAppointmentPrice();
        }

        var appointment = new Appointment(from, to, appointmentPrice, AppointmentStatus.BOOKED, employmentContract);

        var patient = patientRepository.findById(patientId).orElseThrow(
                () -> new BusinessException(PATIENT_WITH_ID + patientId+ DOES_NOT_EXIST));
        appointment.setPatient(patient);

        save(appointment);

        sendEmailRegardingScheduledAppointment(employeeType, appointment);

        return appointment;
    }

    private void sendEmailRegardingScheduledAppointment(EmployeeType employeeType, Appointment appointment) {
        if (employeeType == EmployeeType.DERMATOLOGIST) {
            this.emailService.sendDermatologistAppointmentScheduledMessage(appointment);
        }
        else {
            this.emailService.sendPharmacistAppointmentScheduledMessage(appointment);
        }
    }

    @Override
    public Page<Appointment> getSearchAndFilterExaminedPatients(String firstName, String lastName, LocalDateTime from, LocalDateTime to,
                                                                Pageable pageable, Long employeeId) {
        firstName = firstName == null ? "" : firstName.trim();
        lastName = lastName == null ?  "" : lastName.trim();

        firstName = "%" + firstName + "%";
        lastName = "%" + lastName + "%";

        return this.appointmentRepository.getSearchAndFilterExaminedPatients(firstName.toLowerCase(), lastName.toLowerCase(), from, to, employeeId,
                pageable);
    }

    @Override
    public List<Appointment> getAllAvailableDermatologistAppointments(LocalDateTime fromTime, LocalDateTime toTime, Long employeeId, Long patientId) {
        List<Appointment> dermatologistAppointments = this.appointmentRepository.getAllAvailableDermatologistAppointmentsForDateRange(employeeId,
                fromTime, toTime);

        List<Appointment> patientAppointments = this.appointmentRepository.getAllBookedAppointmentsForPatientForRange(patientId,
                fromTime, toTime);

        List<Appointment> newAppointments = new ArrayList<>();
        for(Appointment dermatologistAppointment: dermatologistAppointments) {
            var notOverlapping = true;
            for(Appointment patientAppointment: patientAppointments) {
                if (AppointmentOverlapping.areAppointmentsOverlapping(dermatologistAppointment, patientAppointment)) {
                    notOverlapping = false;
                    break;
                }
            }

            if (notOverlapping) {
                newAppointments.add(dermatologistAppointment);
            }
        }

        return  newAppointments;
    }

    @Override
    public List<Appointment> getAllBookedAppointmentsForPatient(Long patientId) {
        return this.appointmentRepository.getAllBookedAppointmentsForPatient(patientId, LocalDateTime.now());
    }

    @Override
    public List<Appointment> getAllBusyAppointmentsForEmployeeForRange(Long employeeId, LocalDateTime from, LocalDateTime to) {
        return appointmentRepository.getAllBusyAppointmentsForEmployeeForRange(employeeId, from, to);
    }

    @Override
    public void cancelAppointment(Long id, Long appointmentId) {
        var patient = this.patientRepository.findActivePatient(id, true);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NO_PATIENT);
        }
        var appointment = this.appointmentRepository.getAppointmentByIdAndActiveTrue(appointmentId)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, NO_APPOINTMENT);
                });

        if (appointment.getPatient() == null || !appointment.getPatient().equals(patient)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This appointment is not scheduled by you and it can't be canceled.");
        }

        if (!appointment.getAppointmentStatus().equals(AppointmentStatus.BOOKED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Appointment is not scheduled and can't be canceled.");
        }

        if (LocalDateTime.now().plusDays(1).isAfter(appointment.getFrom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only appointments that are due in the more than 24h can be canceled.");
        }

        appointment.setAppointmentStatus(AppointmentStatus.AVAILABLE);
        appointment.setPatient(null);
    }

    @Override
    public Page<Appointment> getScheduledAppointments(Long id, EmployeeType employeeType, String name, Pageable pageable) {
        var patient = this.patientRepository.findActivePatient(id, true);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NO_PATIENT);
        }

        return this.appointmentRepository.getScheduledAppointmentsForPatient(id, employeeType, name,
                AppointmentStatus.BOOKED, LocalDateTime.now(), pageable);
    }

}

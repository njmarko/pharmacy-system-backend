package com.mrsisa.pharmacy.controller;


import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import com.mrsisa.pharmacy.dto.*;
import com.mrsisa.pharmacy.dto.appointment.*;
import com.mrsisa.pharmacy.dto.employee.EmployeeBusyDaysDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveRequestDateDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveRequestDatesDTO;
import com.mrsisa.pharmacy.dto.patient.ExaminedPatientDTO;
import com.mrsisa.pharmacy.dto.patient.ExaminedPatientSearchDTO;
import com.mrsisa.pharmacy.dto.patient.PatientNotShowedUpDTO;
import com.mrsisa.pharmacy.service.*;
import com.mrsisa.pharmacy.support.IConverter;
import com.mrsisa.pharmacy.validation.validator.IAppointmentConclusionValidator;
import com.mrsisa.pharmacy.validation.validator.IBusyDatesValidator;
import com.mrsisa.pharmacy.validation.validator.IDermatologistAvailableAppointmentScheduleValidator;
import com.mrsisa.pharmacy.validation.validator.IEmployeeAppointmentSchedulingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/appointments")
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private final IEmploymentContractService employmentContractService;
    private final IPatientService patientService;
    private final ILeaveDaysRequestService leaveDaysRequestService;

    private final IAppointmentConclusionValidator appointmentConclusionValidator;
    private final IBusyDatesValidator busyDatesValidator;
    private final IEmployeeAppointmentSchedulingValidator employeeAppointmentSchedulingValidator;
    private final IDermatologistAvailableAppointmentScheduleValidator dermatologistAvailableAppointmentScheduleValidator;

    private final IConverter<Appointment, AppointmentRangeResultDTO> toAppointmentRangeResultDTO;
    private final IConverter<Appointment, AppointmentStartDTO> toAppointmentStartDTO;
    private final IConverter<Appointment, AppointmentDatesDTO> toAppointmentDates;
    private final IConverter<WorkingDay, WorkingDayTimeDTO> toWorkingDayTimeDTO;
    private final IConverter<Appointment, ExaminedPatientDTO> toExaminedPatientDTO;
    private final IConverter<LeaveDaysRequest, LeaveRequestDateDTO> toLeaveRequestDateDTO;

    @Autowired
    public AppointmentController(IAppointmentService appointmentService,
                                 IEmploymentContractService employmentContractService,
                                 IPatientService patientService,
                                 IConverter<Appointment, AppointmentRangeResultDTO> toAppointmentRangeResultDTO,
                                 IConverter<Appointment, AppointmentStartDTO> toAppointmentStartDTO,
                                 IAppointmentConclusionValidator appointmentConclusionValidator,
                                 IBusyDatesValidator busyDatesValidator,
                                 IConverter<Appointment, AppointmentDatesDTO> toAppointmentDates,
                                 IConverter<WorkingDay, WorkingDayTimeDTO> toWorkingDayTimeDTO,
                                 IEmployeeAppointmentSchedulingValidator employeeAppointmentSchedulingValidator,
                                 IConverter<Appointment, ExaminedPatientDTO> toExaminedPatientDTO,
                                 IDermatologistAvailableAppointmentScheduleValidator dermatologistAvailableAppointmentScheduleValidator,
                                 ILeaveDaysRequestService leaveDaysRequestService,
                                 IConverter<LeaveDaysRequest, LeaveRequestDateDTO> toLeaveRequestDateDTO) {
        this.appointmentService = appointmentService;
        this.employmentContractService = employmentContractService;
        this.patientService = patientService;
        this.toAppointmentRangeResultDTO = toAppointmentRangeResultDTO;
        this.toAppointmentStartDTO = toAppointmentStartDTO;
        this.appointmentConclusionValidator = appointmentConclusionValidator;
        this.busyDatesValidator = busyDatesValidator;
        this.toAppointmentDates = toAppointmentDates;
        this.toWorkingDayTimeDTO = toWorkingDayTimeDTO;
        this.employeeAppointmentSchedulingValidator = employeeAppointmentSchedulingValidator;
        this.toExaminedPatientDTO = toExaminedPatientDTO;
        this.dermatologistAvailableAppointmentScheduleValidator = dermatologistAvailableAppointmentScheduleValidator;
        this.leaveDaysRequestService = leaveDaysRequestService;
        this.toLeaveRequestDateDTO = toLeaveRequestDateDTO;
    }

    @GetMapping(value = "/allDermatologistAppointments/{id}")
    @PreAuthorize("hasRole('ROLE_DERMATOLOGIST')")
    @OwningUser
    public CalendarDatesDTO getAllDermatologistAppointments(@PathVariable("id") Long id,
                                                                           @RequestParam("dateFrom") String fromTime,
                                                                           @RequestParam("dateTo") String toTime,
                                                                           @RequestParam("pharmacyId") Long pharmacyId) {
        List<Appointment> appointments = appointmentService.getAppointmentsForEmployee(pharmacyId, id,
                LocalDateTime.parse(fromTime, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse(toTime, DateTimeFormatter.ISO_DATE_TIME), EmployeeType.DERMATOLOGIST);

        return getCalendarDatesDTO(id, appointments);
    }

    @GetMapping(value = "/allPharmacistAppointments/{id}")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    @OwningUser
    public CalendarDatesDTO getAllPharmacistAppointments(@PathVariable("id") Long id,
                                                                        @RequestParam("dateFrom") String fromTime,
                                                                        @RequestParam("dateTo") String toTime) {
        var employmentContract = employmentContractService.getPharmacistContract(id);
        List<Appointment> appointments = appointmentService.getAppointmentsForEmployee(employmentContract.getPharmacy().getId(), id,
                LocalDateTime.parse(fromTime, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse(toTime, DateTimeFormatter.ISO_DATE_TIME), EmployeeType.PHARMACIST);

        return getCalendarDatesDTO(id, appointments);
    }

    private CalendarDatesDTO getCalendarDatesDTO(@PathVariable("id") Long id, List<Appointment> appointments) {
        List<LeaveDaysRequest> leaveDaysRequests = leaveDaysRequestService.getAllPendingAndAcceptedLeaveDaysRequestForEmployee(id);

        List<AppointmentRangeResultDTO> appointmentRangeResultDTOS = appointments.stream().map(toAppointmentRangeResultDTO::convert).collect(Collectors.toList());
        List<LeaveRequestDateDTO> leaveRequestDateDTOS = leaveDaysRequests.stream().map(
                toLeaveRequestDateDTO::convert).collect(Collectors.toList());

        return new CalendarDatesDTO(appointmentRangeResultDTOS, leaveRequestDateDTOS);
    }

    @GetMapping(value = "/allAvailableDermatologistAppointments/{id}")
    @PreAuthorize("hasRole('ROLE_DERMATOLOGIST')")
    @OwningUser
    public List<AppointmentRangeResultDTO> getAllAvailableDermatologistAppointments(@PathVariable("id") Long id,
                                                                           @RequestParam("dateFrom") String fromTime,
                                                                           @RequestParam("dateTo") String toTime,
                                                                           @RequestParam("patientId") Long patientId,
                                                                           @RequestParam("pharmacyId") Long pharmacyId) {
        var fromDate =  LocalDateTime.parse(fromTime, DateTimeFormatter.ISO_DATE_TIME);
        var toDate = LocalDateTime.parse(toTime, DateTimeFormatter.ISO_DATE_TIME);

        var employmentContract = employmentContractService.getContractWithPharmacy(id, pharmacyId);

        var appointments = appointmentService.getAllAvailableDermatologistAppointments(fromDate, toDate,
                employmentContract.getId(), patientId);

        return appointments.stream().map(toAppointmentRangeResultDTO::convert).collect(Collectors.toList());
    }

    @PutMapping(value = "/scheduleAvailableDermatologistAppointment/{id}")
    @PreAuthorize("hasRole('ROLE_DERMATOLOGIST')")
    @OwningUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void scheduleAvailableDermatologistAppointment(@PathVariable("id") Long id,
                                                          @Valid @RequestBody AvailableAppointmentSchedulingDTO dto) {
        var employmentContract = employmentContractService.getContractWithPharmacy(id, dto.getPharmacyId());
        dto.setEmployeeId(employmentContract.getId());

        dermatologistAvailableAppointmentScheduleValidator.isValid(dto);

        appointmentService.scheduleAvailableDermatologistAppointment(dto.getAppointmentId(), dto.getPatientId());
    }

    @GetMapping(value = "/beginAppointment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    public AppointmentStartDTO getAppointmentInProgress(@PathVariable("id") Long id) {
        var appointment = appointmentService.getAppointmentInProgressForEmployee(id);
        return toAppointmentStartDTO.convert(appointment);
    }

    @GetMapping(value = "/allExamined/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    public Page<ExaminedPatientDTO> getExaminedPatients(@PathVariable("id") Long id,
                                                        ExaminedPatientSearchDTO examinedPatientSearchDTO,
                                                        @PageableDefault Pageable page) {
        return this.appointmentService.getSearchAndFilterExaminedPatients(examinedPatientSearchDTO.getFirstName(),
                examinedPatientSearchDTO.getLastName(), examinedPatientSearchDTO.getFromTime(), examinedPatientSearchDTO.getToTime(),
                page, id).map(toExaminedPatientDTO::convert);
    }

    @GetMapping(value = "/busyDates/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    public BusyDatesDTO getBusyDatesForEmployeeAndPatient(@PathVariable("id") Long id,
                                                          @RequestParam("patientId") Long patientId,
                                                          @RequestParam("pharmacyId") Long pharmacyId) {
        var employeeAppointmentSchedulingDTO = new EmployeeBusyDaysDTO();
        employeeAppointmentSchedulingDTO.setPatientId(patientId);
        employeeAppointmentSchedulingDTO.setPharmacyId(pharmacyId);
        employeeAppointmentSchedulingDTO.setEmployeeId(id);

        busyDatesValidator.isValid(employeeAppointmentSchedulingDTO);

        var bookedEmployeeAppointments = this.appointmentService.getAllBusyAppointmentsForEmployee(employeeAppointmentSchedulingDTO.getEmployeeId());
        var bookedPatientAppointments = this.appointmentService.getAllBookedAppointmentsForPatientNotWithEmployee(employeeAppointmentSchedulingDTO.getPatientId(),
                employeeAppointmentSchedulingDTO.getEmployeeId());

        List<Appointment> allAppointments = new ArrayList<>(bookedEmployeeAppointments.size() + bookedPatientAppointments.size());
        allAppointments.addAll(bookedEmployeeAppointments);
        allAppointments.addAll(bookedPatientAppointments);

        var workingDays = new ArrayList<>(employmentContractService.getContractWithPharmacy(employeeAppointmentSchedulingDTO.getEmployeeId(),
                employeeAppointmentSchedulingDTO.getPharmacyId()).getWorkingHours());

        var appointmentDTOS = allAppointments.stream()
                .map(toAppointmentDates::convert).collect(Collectors.toList());

        var workingDayTimeDTOS = workingDays.stream()
                .map(toWorkingDayTimeDTO::convert).collect(Collectors.toList());


        return new BusyDatesDTO(workingDayTimeDTOS, appointmentDTOS);
     }

    @GetMapping(value = "/leaveDates/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    public LeaveRequestDatesDTO getAllLeaveDates(@PathVariable("id") Long id,
                                                 @RequestParam("dateFrom") String fromTime,
                                                 @RequestParam("dateTo") String toTime) {
        var fromDate =  LocalDateTime.parse(fromTime, DateTimeFormatter.ISO_DATE_TIME);
        var toDate = LocalDateTime.parse(toTime, DateTimeFormatter.ISO_DATE_TIME);


        var bookedEmployeeAppointments = appointmentService.getAllBusyAppointmentsForEmployeeForRange(id,
                fromDate, toDate);

        var leaveDaysRequests = leaveDaysRequestService.getAllPendingAndAcceptedLeaveDaysRequestForEmployeeForRange(id,
                fromDate, toDate);

        var appointmentDTOS = bookedEmployeeAppointments.stream()
                .map(toAppointmentRangeResultDTO::convert).collect(Collectors.toList());

        var leaveRequestDateDTOS = leaveDaysRequests.stream().map(
                toLeaveRequestDateDTO::convert).collect(Collectors.toList());


        return new LeaveRequestDatesDTO(appointmentDTOS, leaveRequestDateDTOS);
    }



    @PutMapping(value = "/patientNotShowedUp/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patientNotShowedUp(@PathVariable("id") Long id, @RequestBody PatientNotShowedUpDTO dto) {
        appointmentService.checkIfFillingThatPatientHasNotShowedUpIsValid(dto.getAppointmentId(), dto.getPatientId(), id);
        patientService.patientNotShowedUp(dto.getPatientId(), dto.getAppointmentId());
    }

    @PostMapping(value = "/concludeAppointment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void concludeAppointment(@PathVariable("id") Long id, @Valid @RequestBody AppointmentConclusionDTO appointmentConclusionDTO) {
        appointmentConclusionDTO.setEmployeeId(id);
        appointmentConclusionValidator.isValid(appointmentConclusionDTO);
        appointmentService.concludeAppointment(appointmentConclusionDTO.getReportText(), appointmentConclusionDTO.getPharmacyId(), appointmentConclusionDTO.getPatientId(),
                appointmentConclusionDTO.getAppointmentId(), appointmentConclusionDTO.getMedicineStocks());

    }

    @PostMapping(value="/scheduleEmployeeAppointment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void scheduleEmployeeAppointment(@PathVariable("id") Long id, @Valid @RequestBody EmployeeAppointmentSchedulingDTO employeeAppointmentSchedulingDTO) {
        employeeAppointmentSchedulingDTO.setEmployeeId(id);
        employeeAppointmentSchedulingValidator.isValid(employeeAppointmentSchedulingDTO);

        var employmentContract = employmentContractService.getContractWithPharmacy(id, employeeAppointmentSchedulingDTO.getPharmacyId());
        appointmentService.scheduleAppointmentForEmployee(employeeAppointmentSchedulingDTO.getFrom(), employeeAppointmentSchedulingDTO.getTo(),
                employeeAppointmentSchedulingDTO.getPatientId(), employmentContract, employeeAppointmentSchedulingDTO.getPharmacyId());

    }

    @GetMapping(value = "/busyDatesEmployee/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    public List<AppointmentDatesDTO> getBusyDatesForEmployee(@PathVariable("id") Long id) {
        return this.appointmentService.getAllBusyAppointmentsForEmployee(id).stream()
                .map(toAppointmentDates::convert).collect(Collectors.toList());
    }
}

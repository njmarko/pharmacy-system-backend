package com.mrsisa.pharmacy.validation.validator.impl;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import com.mrsisa.pharmacy.dto.appointment.EmployeeAppointmentSchedulingDTO;
import com.mrsisa.pharmacy.service.IAppointmentService;
import com.mrsisa.pharmacy.service.IEmploymentContractService;
import com.mrsisa.pharmacy.service.ILeaveDaysRequestService;
import com.mrsisa.pharmacy.util.AppointmentOverlapping;
import com.mrsisa.pharmacy.validation.validator.IEmployeeAppointmentSchedulingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeAppointmentSchedulingValidator implements IEmployeeAppointmentSchedulingValidator {
    private final IAppointmentService appointmentService;
    private final IEmploymentContractService employmentContractService;
    private final ILeaveDaysRequestService leaveDaysRequestService;

    @Autowired
    public EmployeeAppointmentSchedulingValidator(IAppointmentService appointmentService,
                                                  IEmploymentContractService employmentContractService,
                                                  ILeaveDaysRequestService leaveDaysRequestService) {
        this.appointmentService = appointmentService;
        this.employmentContractService = employmentContractService;
        this.leaveDaysRequestService = leaveDaysRequestService;
    }

    @Override
    public void isValid(EmployeeAppointmentSchedulingDTO employeeAppointmentSchedulingDTO) {
        var employmentContract = employmentContractService.getContractWithPharmacy(employeeAppointmentSchedulingDTO.getEmployeeId(),
                employeeAppointmentSchedulingDTO.getPharmacyId());
        appointmentService.getScheduledAppointmentForPatientAndEmployee(employmentContract.getId(), employeeAppointmentSchedulingDTO.getPatientId());

        checkFromAndToDates(employeeAppointmentSchedulingDTO);

        checkWorkingDays(employeeAppointmentSchedulingDTO);

        checkPatientAppointmentsOverlapping(employeeAppointmentSchedulingDTO);

        checkEmployeeAppointmentsOverlapping(employeeAppointmentSchedulingDTO);

        checkLeaveDaysRequestOverlapping(employeeAppointmentSchedulingDTO);
    }

    private void checkFromAndToDates(EmployeeAppointmentSchedulingDTO employeeAppointmentSchedulingDTO) {
        var from = employeeAppointmentSchedulingDTO.getFrom();
        var to = employeeAppointmentSchedulingDTO.getTo();


        if (from.isBefore(LocalDateTime.now()) || to.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates are before today's date!");
        }

        if (to.isBefore(from) || to.isEqual(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date before or equal to start date!");
        }

        if (!from.toLocalDate().isEqual(to.toLocalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is not at the same day as end date!");
        }
    }

    private void checkLeaveDaysRequestOverlapping(EmployeeAppointmentSchedulingDTO employeeAppointmentSchedulingDTO) {
        var from = employeeAppointmentSchedulingDTO.getFrom();
        var to = employeeAppointmentSchedulingDTO.getTo();

        var firstAppointment = new Appointment(from, to,
                0.0, AppointmentStatus.BOOKED,
                null);

        List<LeaveDaysRequest> leaveDaysRequests = leaveDaysRequestService.getAllPendingAndAcceptedLeaveDaysRequestForEmployee(employeeAppointmentSchedulingDTO.getEmployeeId());

        leaveDaysRequests.forEach(request -> {
            LocalDateTime secondFrom = request.getFrom().atStartOfDay();
            LocalDateTime secondTo = request.getTo().atTime(23, 59);

            var secondAppointment = new Appointment(secondFrom, secondTo,
                    0.0, AppointmentStatus.BOOKED,
                    null);

            if (AppointmentOverlapping.areAppointmentsOverlapping(firstAppointment, secondAppointment)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is overlapping with leave days request!");
            }
        });
    }

    private void checkWorkingDays(EmployeeAppointmentSchedulingDTO employeeAppointmentSchedulingDTO) {
        var from = employeeAppointmentSchedulingDTO.getFrom();
        var to = employeeAppointmentSchedulingDTO.getTo();
        var dayOfWeek = employeeAppointmentSchedulingDTO.getFrom().getDayOfWeek();

        var fromTime = LocalTime.of(from.getHour(), from.getMinute());
        var toTime = LocalTime.of(to.getHour(), to.getMinute());

        List<WorkingDay> workingDays = new ArrayList<>(employmentContractService.getContractWithPharmacy(employeeAppointmentSchedulingDTO.getEmployeeId(),
                employeeAppointmentSchedulingDTO.getPharmacyId()).getWorkingHours());

        workingDays.forEach(workingDay -> {
            if (workingDay.getDay() == dayOfWeek) {
                LocalTime workingFrom = workingDay.getFromHours();
                LocalTime workingTo = workingDay.getToHours();

                if (fromTime.isBefore(workingFrom) || fromTime.isAfter(workingTo)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment start time is outside working hours!");
                }

                if (toTime.isBefore(workingFrom) || toTime.isAfter(workingTo)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment end time is outside working hours!");
                }
            }
        });
    }

    private void checkPatientAppointmentsOverlapping(EmployeeAppointmentSchedulingDTO employeeAppointmentSchedulingDTO) {
        List<Appointment> bookedAppointments = appointmentService.getAllBookedAppointmentsForPatientNotWithEmployee(employeeAppointmentSchedulingDTO.getPatientId(),
                employeeAppointmentSchedulingDTO.getEmployeeId());

        bookedAppointments.forEach(appointment -> checkAppointmentOverlapping(employeeAppointmentSchedulingDTO, appointment));
    }

    private void checkEmployeeAppointmentsOverlapping(EmployeeAppointmentSchedulingDTO employeeAppointmentSchedulingDTO) {
        List<Appointment> bookedAppointments = appointmentService.getAllBusyAppointmentsForEmployee(employeeAppointmentSchedulingDTO.getEmployeeId());

        bookedAppointments.forEach(appointment -> checkAppointmentOverlapping(employeeAppointmentSchedulingDTO, appointment));
    }

    private void checkAppointmentOverlapping(EmployeeAppointmentSchedulingDTO employeeAppointmentSchedulingDTO, Appointment appointment) {
        var firstAppointment = new Appointment(employeeAppointmentSchedulingDTO.getFrom(), employeeAppointmentSchedulingDTO.getTo(),
                0.0, AppointmentStatus.BOOKED,
                null);

        if (AppointmentOverlapping.areAppointmentsOverlapping(firstAppointment, appointment)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is overlapping with other appointment!");
        }
    }
}

package com.mrsisa.pharmacy.validation.validator.impl;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestCreateDTO;
import com.mrsisa.pharmacy.service.IAppointmentService;
import com.mrsisa.pharmacy.service.IEmploymentContractService;
import com.mrsisa.pharmacy.service.ILeaveDaysRequestService;
import com.mrsisa.pharmacy.service.IPharmacyEmployeeService;
import com.mrsisa.pharmacy.util.AppointmentOverlapping;
import com.mrsisa.pharmacy.validation.validator.ILeaveDaysRequestCreateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Component
public class LeaveDaysRequestCreateValidator implements ILeaveDaysRequestCreateValidator {
    private final IAppointmentService appointmentService;
    private final ILeaveDaysRequestService leaveDaysRequestService;
    private final IEmploymentContractService employmentContractService;
    private final IPharmacyEmployeeService pharmacyEmployeeService;

    @Autowired
    public LeaveDaysRequestCreateValidator(IAppointmentService appointmentService,
                                           ILeaveDaysRequestService leaveDaysRequestService,
                                           IEmploymentContractService employmentContractService,
                                           IPharmacyEmployeeService employeeService) {
        this.appointmentService = appointmentService;
        this.leaveDaysRequestService = leaveDaysRequestService;
        this.employmentContractService = employmentContractService;
        this.pharmacyEmployeeService = employeeService;
    }

    @Override
    public void isValid(LeaveDaysRequestCreateDTO dto) {
        checkEmployee(dto);

        checkForDates(dto);

        checkAppointmentOverlapping(dto);

        checkPendingAndApprovedRequestsOverlapping(dto);
    }

    private void checkEmployee(LeaveDaysRequestCreateDTO dto) {
        var pharmacyEmployee = pharmacyEmployeeService.get(dto.getEmployeeId());
        List<EmploymentContract> employmentContracts = employmentContractService.getEmployeeContractsList(pharmacyEmployee);

        if (employmentContracts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee does not work at any pharmacy!");
        }
    }

    private void checkPendingAndApprovedRequestsOverlapping(LeaveDaysRequestCreateDTO dto) {
        var from = dto.getFrom().atStartOfDay();
        var to = dto.getTo().atTime(23, 59);

        var firstAppointment = new Appointment(from, to,
                0.0, AppointmentStatus.BOOKED,
                null);

        List<LeaveDaysRequest> pendingRequests = leaveDaysRequestService.getAllPendingAndAcceptedLeaveDaysRequestForEmployee(dto.getEmployeeId());
        pendingRequests.forEach(request -> {
            var secondFrom = request.getFrom().atStartOfDay();
            var secondTo = request.getTo().atTime(23, 59);

            var secondAppointment = new Appointment(secondFrom, secondTo,
                    0.0, AppointmentStatus.BOOKED,
                    null);

            if (AppointmentOverlapping.areAppointmentsOverlapping(firstAppointment, secondAppointment)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is overlapping with another request!");
            }
        });
    }

    private void checkForDates(LeaveDaysRequestCreateDTO dto) {
        var now = LocalDate.now();

        if (dto.getFrom().isAfter(dto.getTo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date is before start date!");
        }

        if (dto.getFrom().isEqual(dto.getTo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date is equal to start date!");
        }

        if (dto.getFrom().isBefore(now) || dto.getTo().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some of the dates are in the past!");
        }
    }

    private void checkAppointmentOverlapping(LeaveDaysRequestCreateDTO dto) {
        var from = dto.getFrom().atStartOfDay();
        var to = dto.getTo().atTime(23, 59);


        var firstAppointment = new Appointment(from, to,
                0.0, AppointmentStatus.BOOKED,
                null);

        List<Appointment> busyAppointments = appointmentService.getAllBusyAppointmentsForEmployee(dto.getEmployeeId());
        busyAppointments.forEach(busyAppointment -> {
            if (AppointmentOverlapping.areAppointmentsOverlapping(firstAppointment, busyAppointment)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Leave dates are overlapping with other appointment!");
            }
        });
    }
}

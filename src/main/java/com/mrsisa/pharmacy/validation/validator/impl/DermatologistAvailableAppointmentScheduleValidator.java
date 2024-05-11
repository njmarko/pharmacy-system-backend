package com.mrsisa.pharmacy.validation.validator.impl;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.dto.appointment.AvailableAppointmentSchedulingDTO;
import com.mrsisa.pharmacy.service.IAppointmentService;
import com.mrsisa.pharmacy.util.AppointmentOverlapping;
import com.mrsisa.pharmacy.validation.validator.IDermatologistAvailableAppointmentScheduleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DermatologistAvailableAppointmentScheduleValidator implements IDermatologistAvailableAppointmentScheduleValidator {
    private final IAppointmentService appointmentService;

    @Autowired
    public DermatologistAvailableAppointmentScheduleValidator(IAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }


    @Override
    public void isValid(AvailableAppointmentSchedulingDTO dto) {
        checkAppointmentValidity(dto);

        checkPatientAppointmentOverlapping(dto);
    }

    private void checkAppointmentValidity(AvailableAppointmentSchedulingDTO dto) {
        var appointment = appointmentService.get(dto.getAppointmentId());

        if (!appointment.getAppointmentStatus().equals(AppointmentStatus.AVAILABLE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is not available!");
        }

        if(!appointment.getEmployee().getId().equals(dto.getEmployeeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not your appointment!");
        }

        if(appointment.getFrom().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is in the past!");
        }

    }

    private void checkPatientAppointmentOverlapping(AvailableAppointmentSchedulingDTO dto) {
        var dermatologistAppointment = appointmentService.get(dto.getAppointmentId());
        List<Appointment> patientAppointments = appointmentService.getAllBookedAppointmentsForPatient(dto.getPatientId());

        patientAppointments.forEach(appointment -> {
            if (AppointmentOverlapping.areAppointmentsOverlapping(dermatologistAppointment, appointment)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is overlapping with patient appointment");
            }
        });
    }
}

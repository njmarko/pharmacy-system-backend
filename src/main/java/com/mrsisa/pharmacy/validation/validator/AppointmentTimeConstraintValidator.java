package com.mrsisa.pharmacy.validation.validator;

import com.mrsisa.pharmacy.dto.appointment.AvailableAppointmentCreationDTO;
import com.mrsisa.pharmacy.validation.constraint.AppointmentTimeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AppointmentTimeConstraintValidator implements ConstraintValidator<AppointmentTimeConstraint, AvailableAppointmentCreationDTO> {
    @Override
    public void initialize(AppointmentTimeConstraint constraintAnnotation) {
        // Empty
    }

    @Override
    public boolean isValid(AvailableAppointmentCreationDTO dto, ConstraintValidatorContext constraintValidatorContext) {
        if (dto.getFromTime() == null || dto.getToTime() == null) {
            return false;
        }
        if (dto.getFromTime().isBefore(LocalDateTime.now()) || dto.getToTime().isBefore(LocalDateTime.now())) {
            return false;
        }
        if (dto.getToTime().isBefore(dto.getFromTime())) {
            return false;
        }
        if (!dto.getToTime().toLocalDate().equals(dto.getFromTime().toLocalDate())) {
            return false;
        }
        return !dto.getToTime().equals(dto.getFromTime());
    }
}

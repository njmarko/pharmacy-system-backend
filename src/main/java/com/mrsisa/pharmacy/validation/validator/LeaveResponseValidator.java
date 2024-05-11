package com.mrsisa.pharmacy.validation.validator;

import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestResponseDTO;
import com.mrsisa.pharmacy.validation.constraint.LeaveResponseConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LeaveResponseValidator implements ConstraintValidator<LeaveResponseConstraint, LeaveDaysRequestResponseDTO> {
    @Override
    public void initialize(LeaveResponseConstraint constraint) {
        // Empty
    }

    public boolean isValid(LeaveDaysRequestResponseDTO response, ConstraintValidatorContext context) {
        if (response.getAccepted() == null) {
            return false;
        }
        if (Boolean.TRUE.equals(response.getAccepted())) {
            return true;
        }
        return (response.getRejectionReason() != null) && (!response.getRejectionReason().isBlank());
    }
}

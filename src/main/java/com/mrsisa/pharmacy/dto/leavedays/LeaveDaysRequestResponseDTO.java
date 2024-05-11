package com.mrsisa.pharmacy.dto.leavedays;

import com.mrsisa.pharmacy.validation.constraint.LeaveResponseConstraint;
import lombok.Data;

@Data
@LeaveResponseConstraint
public class LeaveDaysRequestResponseDTO {

    private Boolean accepted;
    private String rejectionReason;
}

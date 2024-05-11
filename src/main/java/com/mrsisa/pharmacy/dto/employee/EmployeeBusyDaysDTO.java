package com.mrsisa.pharmacy.dto.employee;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class EmployeeBusyDaysDTO {
    @NotNull(message = "Patient id cant be null.")
    @Min(value = 0, message = "Patient id cant be less than zero.")
    private Long patientId;

    @NotNull(message = "Pharmacy id cant be null.")
    @Min(value = 0, message = "Pharmacy id cant be less than zero.")
    private Long pharmacyId;

    private Long employeeId;
}

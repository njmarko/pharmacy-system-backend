package com.mrsisa.pharmacy.dto.appointment;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AvailableAppointmentSchedulingDTO {
    @NotNull(message = "Appointment id cant be null.")
    @Min(value = 0, message = "Appointment id cant be less than zero.")
    private Long appointmentId;

    @NotNull(message = "Patient id cant be null.")
    @Min(value = 0, message = "Patient id cant be less than zero.")
    private Long patientId;

    @NotNull(message = "Pharmacy id cant be null.")
    @Min(value = 0, message = "Pharmacy id cant be less than zero.")
    private Long pharmacyId;

    private Long employeeId;
}

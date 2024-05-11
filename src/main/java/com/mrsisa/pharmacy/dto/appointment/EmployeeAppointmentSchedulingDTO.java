package com.mrsisa.pharmacy.dto.appointment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateTimeDeserializer;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class EmployeeAppointmentSchedulingDTO {
    @NotNull(message = "Date cant be null.")
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    private LocalDateTime from;
    @NotNull(message = "Date cant be null.")
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    private LocalDateTime to;

    @NotNull(message = "Patient id cant be null.")
    @Min(value = 0, message = "Patient id cant be less than zero.")
    private Long patientId;

    @NotNull(message = "Pharmacy id cant be null.")
    @Min(value = 0, message = "Pharmacy id cant be less than zero.")
    private Long pharmacyId;

    private Long employeeId;
}

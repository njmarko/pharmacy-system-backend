package com.mrsisa.pharmacy.dto.appointment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateTimeDeserializer;
import com.mrsisa.pharmacy.validation.constraint.AppointmentTimeConstraint;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AppointmentTimeConstraint
public class AvailableAppointmentCreationDTO {
    @NotNull(message = "Employee ID can't be null.")
    private Long employeeId;

    @NotNull(message = "Start time can't be null.")
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    private LocalDateTime fromTime;

    @NotNull(message = "End time can't be null.")
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    private LocalDateTime toTime;
}

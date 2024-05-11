package com.mrsisa.pharmacy.dto.leavedays;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateDeserializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class LeaveDaysRequestCreateDTO {
    @NotNull(message = "From date can't be null.")
    @JsonDeserialize(using = ISOLocalDateDeserializer.class)
    private LocalDate from;
    @NotNull(message = "End date can't be null.")
    @JsonDeserialize(using = ISOLocalDateDeserializer.class)
    private LocalDate to;
    private Long employeeId;
}

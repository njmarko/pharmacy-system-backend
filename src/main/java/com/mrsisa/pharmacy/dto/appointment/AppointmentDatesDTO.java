package com.mrsisa.pharmacy.dto.appointment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDatesDTO {
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime from;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime to;
}

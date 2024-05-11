package com.mrsisa.pharmacy.dto.appointment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentStartDTO {

    private Long id;
    private Long employeeId;
    private Long pharmacyId;
    private String pharmacyName;
    private String patientFirstName;
    private String patientLastName;
    private Long patientId;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dateFrom;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dateTo;

}


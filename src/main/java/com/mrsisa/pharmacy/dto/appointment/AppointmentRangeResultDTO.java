package com.mrsisa.pharmacy.dto.appointment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRangeResultDTO {

    private Long id;
    private String employeeFirstName;
    private String employeeLastName;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dateFrom;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dateTo;
    private Long lengthInMinutes;
    private String pharmacyName;
    private String patientFirstName;
    private String patientLastName;
    private AppointmentStatus appointmentStatus;
    private Double employeeAverageGrade;
    private Double price;

}

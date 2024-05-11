package com.mrsisa.pharmacy.dto.appointment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {

    private Long id;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dateFrom;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dateTo;
    private String employeeFirstName;
    private String employeeLastName;
    private String pharmacyName;
    private Double price;
    private EmployeeType employeeType;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime duration;
    private Double employeePharmacyEmployeeAverageGrade;
    private Long employeePharmacyEmployeeId;
    private Integer previousRating;

}

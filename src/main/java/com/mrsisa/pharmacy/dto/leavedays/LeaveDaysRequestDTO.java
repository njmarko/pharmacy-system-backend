package com.mrsisa.pharmacy.dto.leavedays;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveDaysRequestDTO {

    private Long id;
    private String employeeFirstName;
    private String employeeLastName;
    @JsonSerialize(using = ISOLocalDateSerializer.class)
    private LocalDate from;
    @JsonSerialize(using = ISOLocalDateSerializer.class)
    private LocalDate to;

}

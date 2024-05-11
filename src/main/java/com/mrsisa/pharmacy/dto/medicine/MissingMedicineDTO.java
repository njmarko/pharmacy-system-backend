package com.mrsisa.pharmacy.dto.medicine;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MissingMedicineDTO {

    private Integer id;
    private Integer medicineId;
    private String medicineName;
    private String medicineCode;
    private String employeeFirstName;
    private String employeeLastName;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime timeSearched;
    private Double medicineAverageGrade;

}

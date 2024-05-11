package com.mrsisa.pharmacy.dto.patient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.dto.medicine.RecipeMedicineInfoDTO;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ExaminedPatientDTO {
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime from;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime to;
    private Double price;
    private String patientFirstName;
    private String patientLastName;
    private String diagnostics;
    private List<RecipeMedicineInfoDTO> medicines;
}

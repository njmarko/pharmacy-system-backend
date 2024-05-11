package com.mrsisa.pharmacy.dto.promotion;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionCreationResponseDTO {
    private Long id;
    private String content;
    @JsonSerialize(using = ISOLocalDateSerializer.class)
    private LocalDate from;
    @JsonSerialize(using = ISOLocalDateSerializer.class)
    private LocalDate to;
}

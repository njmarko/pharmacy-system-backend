package com.mrsisa.pharmacy.dto.promotion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateDeserializer;
import com.mrsisa.pharmacy.validation.constraint.PromotionConstraint;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@PromotionConstraint
public class PromotionCreationDTO {

    private String content;

    @JsonDeserialize(using = ISOLocalDateDeserializer.class)
    LocalDate from;

    @JsonDeserialize(using = ISOLocalDateDeserializer.class)
    LocalDate to;

    List<PromotionItemDTO> items = new ArrayList<>();

}

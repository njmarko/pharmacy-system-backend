package com.mrsisa.pharmacy.dto.offer;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferUpdateDTO {

    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    @NotNull(message = "Delivery date cannot be null.")
    private LocalDateTime deliveryDate;

    @NotNull(message = "Total cost cannot be null.")
    private Double totalCost;
}

package com.mrsisa.pharmacy.dto.offer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateTimeDeserializer;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferCreationDTO {


    @NotNull(message = "Supplier id cannot be null.")
    private Long supplierId;

    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    @NotNull(message = "Delivery date cannot be null.")
    private LocalDateTime deliveryDate;

    @NotNull(message = "Total cost cannot be null.")
    private Double totalPrice;
}

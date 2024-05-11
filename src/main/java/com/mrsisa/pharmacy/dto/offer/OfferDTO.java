package com.mrsisa.pharmacy.dto.offer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.domain.enums.OfferStatus;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO {

    private Long id;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime deliveryDate;
    private Double totalPrice;
    private OfferStatus status;
    private Long originalOrderId;
    private String supplierCompany;
    private Long supplierId;
}

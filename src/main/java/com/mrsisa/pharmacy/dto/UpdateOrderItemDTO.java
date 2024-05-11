package com.mrsisa.pharmacy.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UpdateOrderItemDTO {

    @Positive(message = "Quantity must be greater than zero.")
    @NotNull(message = "Quantity cannot be null.")
    Integer quantity;

}

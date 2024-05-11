package com.mrsisa.pharmacy.dto;

import com.mrsisa.pharmacy.validation.constraint.OrderItemConstraint;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@OrderItemConstraint
public class AddOrderItemDTO {

    @NotNull(message = "Medicine ID cannot be null.")
    private Long medicineId;
    @Positive(message = "Quantity must be greater than zero.")
    private Integer quantity;
    private Boolean isNew = false;
    private Double newPrice;
}

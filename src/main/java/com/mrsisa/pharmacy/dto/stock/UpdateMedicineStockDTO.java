package com.mrsisa.pharmacy.dto.stock;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UpdateMedicineStockDTO {

    @NotNull(message = "New price can't be null.")
    @Positive(message = "New price must be greater than zero.")
    private Double newPrice;
}

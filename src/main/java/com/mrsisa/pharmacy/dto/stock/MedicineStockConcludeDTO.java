package com.mrsisa.pharmacy.dto.stock;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class MedicineStockConcludeDTO {
    @NotNull(message = "Medicine stock id cant be null.")
    @Min(value = 0, message = "Medicine stock id cant be less than zero.")
    private Long medicineStockId;

    @NotNull(message = "Therapy days cant be null.")
    @Min(value = 1, message = "Therapy days must be greater than zero.")
    private Integer therapyDays;

    @NotNull(message = "Quantity cant be null.")
    @Min(value = 1, message = "Quantity must be greater than zero.")
    private Integer quantity;
}

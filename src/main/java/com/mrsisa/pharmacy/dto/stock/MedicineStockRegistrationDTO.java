package com.mrsisa.pharmacy.dto.stock;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
public class MedicineStockRegistrationDTO {

    @NotBlank
    private String medicineCode;

    @Positive
    private double price;

    @PositiveOrZero
    private int quantity;
}

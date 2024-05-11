package com.mrsisa.pharmacy.dto.employee;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PharmacyEmployeeUpdateDTO {
    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;
}

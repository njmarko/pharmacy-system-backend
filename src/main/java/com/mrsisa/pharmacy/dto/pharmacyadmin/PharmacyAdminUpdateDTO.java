package com.mrsisa.pharmacy.dto.pharmacyadmin;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PharmacyAdminUpdateDTO {

    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

}

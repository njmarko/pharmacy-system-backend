package com.mrsisa.pharmacy.dto.pharmacyadmin;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyAdminRegistrationDTO {

    @NotBlank(message = "First name can't be blank.")
    private String firstName;

    @NotBlank(message = "Last name can't be blank.")
    private String lastName;

    @NotBlank(message = "Username can't be blank.")
    private String username;

    @NotBlank(message = "Password can't be blank.")
    private String password;

    @NotBlank(message = "Email can't be blank.")
    private String email;

    @NotNull(message = "Pharmacy id can't be null.")
    private Long pharmacyId;
}

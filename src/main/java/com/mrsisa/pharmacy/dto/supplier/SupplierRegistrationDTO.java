package com.mrsisa.pharmacy.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRegistrationDTO {

    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotBlank(message = "Last name can't be blank.")
    private String lastName;

    @NotBlank(message = "Password can't be blank.")
    private String password;

    @NotBlank(message = "Username can't be blank.")
    private String username;

    @Email(message = "Email is not valid.")
    private String email;

    @NotBlank(message = "Company can't be blank.")
    private String company;

}

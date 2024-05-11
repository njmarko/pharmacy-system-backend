package com.mrsisa.pharmacy.dto.employee;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRegistrationDTO {

    @NotEmpty(message = "First name cannot be empty.")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty.")
    private String lastName;

    @NotEmpty(message = "Username cannot be empty.")
    private String username;

    @NotEmpty(message = "Password cannot be empty.")
    private String password;

    @Email(message = "Email is not in the correct format.")
    private String email;
}

package com.mrsisa.pharmacy.dto.patient;

import com.mrsisa.pharmacy.domain.valueobjects.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationDTO {

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

    @NotNull(message = "Address cannot be null.")
    @Valid
    private Address address;

    @NotEmpty(message = "Phone number cannot be empty.")
    private String phoneNumber;





}

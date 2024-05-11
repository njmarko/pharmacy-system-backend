package com.mrsisa.pharmacy.dto;

import com.mrsisa.pharmacy.validation.constraint.WorkingHoursConstraint;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class PharmacistRegistrationDTO {

    @NotBlank(message = "First name can't be blank.")
    private String firstName;

    @NotBlank(message = "Last name can't be blank.")
    private String lastName;

    @NotBlank(message = "Password can't be blank.")
    private String password;

    @NotBlank(message = "Username can't be blank.")
    private String username;

    @Email(message = "Email is not valid.")
    private String email;

    @WorkingHoursConstraint
    private List<WorkingDayDTO> workingDays;

}

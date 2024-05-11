package com.mrsisa.pharmacy.dto.supplier;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {

    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

    @NotBlank(message = "Username cannot be blank.")
    private String username;

    @NotBlank(message = "Email cannot be blank.")
    private String email;

    @NotBlank(message = "Company cannot be blank.")
    private String company;
}

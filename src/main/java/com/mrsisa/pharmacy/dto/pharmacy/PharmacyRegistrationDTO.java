package com.mrsisa.pharmacy.dto.pharmacy;

import com.mrsisa.pharmacy.domain.valueobjects.Location;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class PharmacyRegistrationDTO {

    @NotBlank(message = "Pharmacy name cannot be blank.")
    private String name;

    @NotBlank(message = "Pharmacy description cannot be blank.")
    private String description;

    @NotNull(message = "Pharmacy location cannot be null.")
    @Valid
    Location location;
}

package com.mrsisa.pharmacy.dto.patient;

import lombok.Data;

@Data
public class PatientUpdateDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String city;
    private String country;
    private String street;
    private String streetNumber;
    private String zipCode;
}

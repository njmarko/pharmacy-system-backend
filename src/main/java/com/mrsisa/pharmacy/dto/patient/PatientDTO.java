package com.mrsisa.pharmacy.dto.patient;

import lombok.Data;

@Data
public class PatientDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    //number of penalty points
    private Integer numPenalties;
    //loyaltyProgram
    private Integer numPoints;
    private String patientCategoryName;
    private Integer patientCategoryDiscount;
    private String patientCategoryColor;
    //address
    private String city;
    private String country;
    private String street;
    private String streetNumber;
    private String zipCode;

}

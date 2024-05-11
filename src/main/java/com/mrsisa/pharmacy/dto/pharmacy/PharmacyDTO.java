package com.mrsisa.pharmacy.dto.pharmacy;

import lombok.*;

@Data
public class PharmacyDTO {

    private Long id;
    private String name;
    private String description;
    private Double averageGrade;
    private String city;
    private String country;
    private String street;
    private String streetNumber;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    private Double currentPharmacistAppointmentPrice;
    private Double currentDermatologistAppointmentPrice;

}

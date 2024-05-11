package com.mrsisa.pharmacy.domain.valueobjects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Embeddable
@Getter
@Setter
public class Address {

    @NotBlank(message = "Country cannot be blank.")
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank(message = "City cannot be blank.")
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank(message = "Street cannot be blank.")
    @Column(name = "street", nullable = false)
    private String street;


    @NotBlank(message = "Street number cannot be blank.")
    @Column(name = "street_number", nullable = false)
    private String streetNumber;

    @NotBlank(message = "Zip code cannot be blank.")
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    public Address() {
        super();
    }

    public Address(String country, String city, String street, String streetNumber, String zipCode) {
        this();
        this.setCountry(country);
        this.setCity(city);
        this.setStreet(street);
        this.setStreetNumber(streetNumber);
        this.setZipCode(zipCode);
    }
}

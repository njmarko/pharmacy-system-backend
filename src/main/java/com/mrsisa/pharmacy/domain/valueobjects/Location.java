package com.mrsisa.pharmacy.domain.valueobjects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Embeddable
@Getter
@Setter
public class Location {

    @Column(name = "latitude", nullable = false)
    @NotNull(message = "Latitude cannot be null.")
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    @NotNull(message = "Longitude cannot be null.")
    private Double longitude;

    @Embedded
    @Valid
    private Address address;

    public Location() {
        super();
    }

    public Location(Double latitude, Double longitude) {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public Location(Double latitude, Double longitude, Address address) {
        this(latitude, longitude);
        this.setAddress(address);
    }

}

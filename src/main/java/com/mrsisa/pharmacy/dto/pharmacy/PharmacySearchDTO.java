package com.mrsisa.pharmacy.dto.pharmacy;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.PositiveOrZero;

@Data
public class PharmacySearchDTO {
    private String name;
    private String city;
    @PositiveOrZero(message = "Lowest value for low grade must be a positive number or zero.")
    @Max(value = 5, message = "Highest value for low grade must be less than or equal 5.")
    private Double gradeLow = 0d;
    @PositiveOrZero(message = "Lowest value for high grade must be a positive number or zero.")
    @Max(value = 5, message = "Highest value for high grade must be less than or equal 5.")
    private Double gradeHigh = 5d;
    @PositiveOrZero(message = "Distance must be a positive number or zero.")
    private Double distance;
    private Double userLatitude;
    private Double userLongitude;
    // dateTime used for displaying pharmacies with available pharmacist appointments at the specified dateTime
    private String dateTime;
}

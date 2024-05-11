package com.mrsisa.pharmacy.dto.appointment;

import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class UpdateAppointmentPriceDTO {

    @Positive(message = "Pharmacist appointment price must be greater than zero.")
    private Double pharmacistAppointmentPrice;

    @Positive(message = "Dermatologist appointment price must be greater than zero.")
    private Double dermatologistAppointmentPrice;

}

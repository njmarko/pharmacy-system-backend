package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "appointment_price")
@Getter
@Setter
public class AppointmentPrice extends PriceTag {

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    @Column(name = "appointment_type", nullable = false)
    @Enumerated
    private EmployeeType appointmentType;

    public AppointmentPrice() {
        super();
    }

    public AppointmentPrice(Double price, Pharmacy pharmacy, EmployeeType appointmentType) {
        super(price, false);
        this.setPharmacy(pharmacy);
        this.setAppointmentType(appointmentType);
    }

    public AppointmentPrice(Double price, LocalDate from, LocalDate to, Boolean isPromotion, Pharmacy pharmacy, EmployeeType appointmentType) {
        super(price, from, to, isPromotion);
        this.setPharmacy(pharmacy);
        this.setAppointmentType(appointmentType);
    }
}

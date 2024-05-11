package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "missing_medicine_log")
@Getter
@Setter
public class MissingMedicineLog extends BaseEntity {

    @Column(name = "time_searched", nullable = false)
    private LocalDateTime timeSearched;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine missingMedicine;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    private Appointment appointmentSearched;

    public MissingMedicineLog() {
        super();
    }

    public MissingMedicineLog(LocalDateTime timeSearched, Medicine missingMedicine, Appointment appointmentSearched) {
        this();
        this.setTimeSearched(timeSearched);
        this.setMissingMedicine(missingMedicine);
        this.setAppointmentSearched(appointmentSearched);
    }
}

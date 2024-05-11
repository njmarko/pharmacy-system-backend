package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.valueobjects.Report;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Getter
@Setter
public class Appointment extends BaseEntity {

    @Column(name = "from_date", nullable = false)
    private LocalDateTime from;

    @Column(name = "to_date", nullable = false)
    private LocalDateTime to;

    @Formula(value = "to_date-from_date")
    private LocalDateTime duration;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "appointment_status", nullable = false)
    @Enumerated
    private AppointmentStatus appointmentStatus;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Patient patient;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private EmploymentContract employee;

    @Embedded
    private Report report;

    @Version
    private Short version;

    public Appointment() {
        super();
    }

    public Appointment(LocalDateTime from, LocalDateTime to, Double price, AppointmentStatus appointmentStatus,
                       EmploymentContract employee) {
        this();
        this.setFrom(from);
        this.setTo(to);
        this.setPrice(price);
        this.setAppointmentStatus(appointmentStatus);
        this.setEmployee(employee);
    }

    public Appointment(LocalDateTime from, LocalDateTime to, Double price, AppointmentStatus appointmentStatus, Patient patient,
                       EmploymentContract employee, Report report) {
        this(from, to, price, appointmentStatus, employee);
        this.setPatient(patient);
        this.setReport(report);
    }
}

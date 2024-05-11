package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employment_contract")
@Getter
@Setter
public class EmploymentContract extends BaseEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private PharmacyEmployee pharmacyEmployee;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    @OneToMany(mappedBy = "employee", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Appointment> bookedAppointments = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<LeaveDaysRequest> requests = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<WorkingDay> workingHours = new HashSet<>();

    public EmploymentContract() {
        super();
    }

    public EmploymentContract(LocalDate startDate, LocalDate endDate, PharmacyEmployee pharmacyEmployee, Pharmacy pharmacy) {
        this();
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setPharmacyEmployee(pharmacyEmployee);
        this.setPharmacy(pharmacy);
    }
}

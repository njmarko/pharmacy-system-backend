package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pharmacy_employee")
@Getter
@Setter
public class PharmacyEmployee extends User {

    @Column(name = "employee_type", nullable = false)
    @Enumerated
    private EmployeeType employeeType;

    @Column(name = "average_grade", nullable = false)
    private Double averageGrade;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "employee_review")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "employee_complaint")
    private Set<Complaint> complaints = new HashSet<>();

    @OneToMany(mappedBy = "pharmacyEmployee", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<EmploymentContract> contracts = new HashSet<>();

    public PharmacyEmployee() {
        super();
    }

    public PharmacyEmployee(String firstName, String lastName, String username, String password, String email,
                            Boolean verified, Boolean loggedIn, EmployeeType employeeType) {
        super(firstName, lastName, username, password, email, verified, loggedIn);
        this.setEmployeeType(employeeType);
        this.setAverageGrade(0.0);
    }

    public PharmacyEmployee(String firstName, String lastName, String username, String password, String email,
                            Boolean verified, Boolean loggedIn, EmployeeType employeeType, Double averageGrade) {
        this(firstName, lastName, username, password, email, verified, loggedIn, employeeType);
        this.setAverageGrade(averageGrade);
    }
}

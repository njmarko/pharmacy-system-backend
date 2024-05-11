package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.valueobjects.Address;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patient")
@Getter
@Setter
@SQLDelete(sql = "UPDATE patient SET active = false where id = ?")
public class Patient extends User {

    @Column(name = "points", nullable = false)
    private Integer numPoints;

    @Column(name = "penalties", nullable = false)
    private Integer numPenalties;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Embedded
    private Address address;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private PatientCategory patientCategory;

    @OneToMany(mappedBy = "reviewer", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Review> myReviews = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Complaint> complaints = new HashSet<>();

    @ManyToMany(mappedBy = "promotionSubscribers", cascade = {}, fetch = FetchType.LAZY)
    private Set<Pharmacy> subscribedTo = new HashSet<>();

    @ManyToMany(cascade = {}, fetch = FetchType.LAZY)
    @JoinTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"), inverseJoinColumns = @JoinColumn(name = "medicine_id"))
    private Set<Medicine> allergicTo = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Recipe> recipes = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<MedicineReservation> medicineReservations = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Appointment> myAppointments = new HashSet<>();

    public Patient() {
        super();
    }

    public Patient(String firstName, String lastName, String username, String password, String email,
                   Boolean verified, Boolean loggedIn, Integer numPoints, Integer numPenalties, String phoneNumber,
                   PatientCategory patientCategory, Address address) {
        super(firstName, lastName, username, password, email, verified, loggedIn);
        this.setPhoneNumber(phoneNumber);
        this.setNumPoints(numPoints);
        this.setNumPenalties(numPenalties);
        this.setPatientCategory(patientCategory);
        this.setAddress(address);
    }

    public Patient(String firstName, String lastName, String username, String password, String email,
                   Boolean verified, Boolean loggedIn, String phoneNumber, PatientCategory patientCategory, Address address) {
        this(firstName, lastName, username, password, email, verified, loggedIn, 0, 0, phoneNumber, patientCategory, address);
    }

    public void addPoints(Integer points){
        this.setNumPoints(this.getNumPoints() + points);
    }
}

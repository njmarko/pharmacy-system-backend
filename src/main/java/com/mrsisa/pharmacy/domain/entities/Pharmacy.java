package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.valueobjects.Location;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pharmacy")
@Getter
@Setter
public class Pharmacy extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Embedded
    private Location location;

    @Column(name = "average_grade", nullable = false)
    private Double averageGrade;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "pharmacy_review")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "pharmacy_complaint")
    private Set<Complaint> complaints = new HashSet<>();

    @ManyToMany(cascade = {}, fetch = FetchType.LAZY)
    @JoinTable(name = "pharmacy_subscribers", joinColumns = @JoinColumn(name = "pharmacy_id"), inverseJoinColumns = @JoinColumn(name = "patient_id"))
    private Set<Patient> promotionSubscribers = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<PharmacyAdmin> pharmacyAdmins = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<EmploymentContract> employees = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<MedicinePurchase> purchaseHistory = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<MedicineStock> medicineStocks = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<AppointmentPrice> pharmacistAppointmentPrice = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<AppointmentPrice> dermatologistAppointmentPrice = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Promotion> promotions = new HashSet<>();

    @Column(name = "curr_ph_app_price", nullable = false)
    private Double currentPharmacistAppointmentPrice;

    @Column(name = "curr_derm_app_price", nullable = false)
    private Double currentDermatologistAppointmentPrice;

    @Version
    private Short version;

    public Pharmacy() {
        super();
        this.setCurrentDermatologistAppointmentPrice(1000.0);
        this.setCurrentPharmacistAppointmentPrice(1000.0);
    }

    public Pharmacy(String name, String description, Location location, Double averageGrade){
        this();
        this.setName(name);
        this.setDescription(description);
        this.setLocation(location);
        this.setAverageGrade(averageGrade);
    }

    public Pharmacy(String name, String description, Location location){
        this(name, description, location, 0.0);
    }

    public void addPharmacistAppointmentPrice(AppointmentPrice appointmentPrice) {
        this.getPharmacistAppointmentPrice().add(appointmentPrice);
        this.setCurrentPharmacistAppointmentPrice(appointmentPrice.getPrice());
    }

    public void addDermatologistAppointmentPrice(AppointmentPrice appointmentPrice) {
        this.getDermatologistAppointmentPrice().add(appointmentPrice);
        this.setCurrentDermatologistAppointmentPrice(appointmentPrice.getPrice());
    }

    public double getAppointmentPrice(EmployeeType employeeType) {
        switch (employeeType) {
            case DERMATOLOGIST:
                return getCurrentDermatologistAppointmentPrice();
            case PHARMACIST:
                return getCurrentPharmacistAppointmentPrice();
            default:
                return -1d;
        }
    }
}

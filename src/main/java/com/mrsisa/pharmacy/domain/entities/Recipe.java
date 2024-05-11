package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "recipe")
@Getter
@Setter
public class Recipe extends BaseEntity {

    @Column(name = "guid", nullable = false, unique = true)
    private UUID guid;

    @Column(name = "date", nullable = false)
    private LocalDateTime reservationDate;

    @Column(name = "picked_up", nullable = false)
    private Boolean pickedUp;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    private Patient patient;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    @Column(name = "price", nullable = false)
    private Double price;

    @OneToMany(mappedBy = "recipe", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<RecipeMedicineInfo> reservedMedicines = new HashSet<>();

    public Recipe() {
        super();
    }

    public Recipe(UUID guid, LocalDateTime date, Boolean pickedUp, Patient patient) {
        this();
        this.setGuid(guid);
        this.setReservationDate(date);
        this.setPickedUp(pickedUp);
        this.setPatient(patient);
        this.setPrice(0.0);
    }

    public Recipe(LocalDateTime date, Boolean pickedUp, Patient patient, Pharmacy pharmacy) {
        this();
        this.setGuid(UUID.randomUUID());
        this.setReservationDate(date);
        this.setPickedUp(pickedUp);
        this.setPatient(patient);
        this.setPharmacy(pharmacy);
        this.setPrice(0.0);
    }

}

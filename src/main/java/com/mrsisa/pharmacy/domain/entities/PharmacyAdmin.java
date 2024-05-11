package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pharmacy_admin")
@Getter
@Setter
public class PharmacyAdmin extends User {

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    @OneToMany(mappedBy = "pharmacyAdmin", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Order> myOrders = new HashSet<>();

    public PharmacyAdmin() {
        super();
    }

    public PharmacyAdmin(String firstName, String lastName, String username, String password, String email,
                         Boolean verified, Boolean loggedIn, Pharmacy pharmacy) {
        super(firstName, lastName, username, password, email, verified, loggedIn);
        this.setPharmacy(pharmacy);
    }
}

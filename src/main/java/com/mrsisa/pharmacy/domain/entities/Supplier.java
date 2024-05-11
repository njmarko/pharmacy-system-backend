package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "supplier")
@Getter
@Setter
public class Supplier extends User {

    @Column(name = "company", nullable = false)
    private String company;

    @OneToMany(mappedBy = "supplier", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Offer> myOffers = new HashSet<>();

    private Supplier() {
        super();
    }

    public Supplier(String firstName, String lastName, String username, String password, String email, Boolean verified,
                    Boolean loggedIn, String company) {
        super(firstName, lastName, username, password, email, verified, loggedIn);
        this.setCompany(company);
    }
}

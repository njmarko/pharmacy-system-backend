package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "system_admin")
@Getter
@Setter
public class SystemAdmin extends User {

    @OneToMany(mappedBy = "systemAdmin", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<ComplaintReply> myReplies = new HashSet<>();

    public SystemAdmin() {
        super();
    }

    public SystemAdmin(String firstName, String lastName, String username, String password, String email, Boolean verified,
                    Boolean loggedIn) {
        super(firstName, lastName, username, password, email, verified, loggedIn);
    }
}

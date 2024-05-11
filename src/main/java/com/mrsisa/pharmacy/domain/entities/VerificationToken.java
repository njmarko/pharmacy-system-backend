package com.mrsisa.pharmacy.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String token;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime expirationDate;

    @OneToOne(targetEntity = Patient.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "patient_id")
    private Patient patient;

    public VerificationToken(Patient patient){
        this.patient = patient;
        this.createdDate = LocalDateTime.now();
        this.token = UUID.randomUUID().toString();
        this.setExpirationDate(this.createdDate.plusDays(EXPIRATION_DAYS));
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(this.getExpirationDate());
    }
}

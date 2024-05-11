package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.ReservationStatus;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineReservationItem;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medicine_reservation")
@Getter
@Setter
public class MedicineReservation extends BaseEntity {

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "reserved_at", nullable = false)
    private LocalDateTime reservedAt;

    @Column(name = "reservation_deadline", nullable = false)
    private LocalDateTime reservationDeadline;

    @Column(name = "status", nullable = false)
    @Enumerated
    private ReservationStatus reservationStatus;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Patient patient;

    @OneToMany(mappedBy = "reservation", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<MedicineReservationItem> reservedMedicines = new HashSet<>();

    @Version
    private Short version;

    public MedicineReservation() {
        super();
    }

    public MedicineReservation(Double price, LocalDateTime reservedAt, LocalDateTime reservationDeadline,
                               ReservationStatus reservationStatus, Pharmacy pharmacy, Patient patient) {
        this.setPrice(price);
        this.setReservedAt(reservedAt);
        this.setReservationDeadline(reservationDeadline);
        this.setReservationStatus(reservationStatus);
        this.setPharmacy(pharmacy);
        this.setPatient(patient);
    }
}

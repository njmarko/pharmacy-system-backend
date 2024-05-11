package com.mrsisa.pharmacy.domain.valueobjects;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.MedicineReservation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "medicine_reservation_item")
@Getter
@Setter
public class MedicineReservationItem extends BaseEntity {

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private MedicineReservation reservation;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "price", nullable = false)
    private Double price;

    public MedicineReservationItem() {
        super();
    }

    public MedicineReservationItem(Integer quantity, Medicine medicine, Double price) {
        this();
        this.setQuantity(quantity);
        this.setMedicine(medicine);
        this.setPrice(price);
    }

    public MedicineReservationItem(MedicineReservation reservation, Integer quantity, Medicine medicine, Double price) {
        this(quantity, medicine, price);
        this.setReservation(reservation);
    }
}

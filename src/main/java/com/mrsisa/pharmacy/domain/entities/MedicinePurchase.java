package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medicine_purchase")
@Getter
@Setter
public class MedicinePurchase extends BaseEntity {

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    @Column(name = "date_purchased", nullable = false)
    private LocalDate datePurchased;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine purchasedMedicine;

    public MedicinePurchase() {
        super();
    }

    public MedicinePurchase(Integer quantity, Double price, Pharmacy pharmacy,
                            LocalDate datePurchased, Medicine purchasedMedicine) {
        this();
        this.setQuantity(quantity);
        this.setPrice(price);
        this.setPharmacy(pharmacy);
        this.setDatePurchased(datePurchased);
        this.setPurchasedMedicine(purchasedMedicine);
    }
}

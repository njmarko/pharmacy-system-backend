package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medicine_stock")
@Getter
@Setter
public class MedicineStock extends BaseEntity {


    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Medicine medicine;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<StockPrice> priceTags = new HashSet<>();

    @Column(name = "current_price", nullable = false)
    private Double currentPrice = 0.0;

    @Column(name = "is_on_promotion", nullable = false)
    private Boolean isCurrentlyOnPromotion = false;

    public MedicineStock() {
        super();
    }

    public MedicineStock(Integer quantity, Pharmacy pharmacy, Medicine medicine) {
        this();
        this.setQuantity(quantity);
        this.setPharmacy(pharmacy);
        this.setMedicine(medicine);
    }

    public void addPriceTag(StockPrice stockPrice) {
        this.getPriceTags().add(stockPrice);
        this.setCurrentPrice(stockPrice.getPrice());
        this.setIsCurrentlyOnPromotion(stockPrice.getIsPromotion());
    }

}

package com.mrsisa.pharmacy.domain.valueobjects;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.Promotion;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "promotion_item")
@Getter
@Setter
public class PromotionItem extends BaseEntity {

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Promotion promotion;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "discount", nullable = false)
    private Integer discount;

    @Column(name = "price_reduction", nullable = false)
    private Double priceReduction;

    public PromotionItem() {
        super();
    }

    public PromotionItem(Promotion promotion, Medicine medicine, Integer discount) {
        this();
        this.setPromotion(promotion);
        this.setMedicine(medicine);
        this.setDiscount(discount);
    }

    public Double getDiscountFactor() {
        return (1 - this.getDiscount() / 100.0);
    }

    public Double getInverseDiscountFactor() {
        return 1.0 / this.getDiscountFactor();
    }
}

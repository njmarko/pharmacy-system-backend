package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.PromotionStatus;
import com.mrsisa.pharmacy.domain.valueobjects.PromotionItem;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promotion")
@Getter
@Setter
public class Promotion extends BaseEntity {

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "status", nullable = false)
    @Enumerated
    private PromotionStatus promotionStatus;

    @OneToMany(mappedBy = "promotion", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<PromotionItem> promotionItems = new HashSet<>();

    public Promotion() {
        super();
    }

    public Promotion(Pharmacy pharmacy, String content, LocalDate from, LocalDate to, PromotionStatus status) {
        this();
        this.setPharmacy(pharmacy);
        this.setContent(content);
        this.setFromDate(from);
        this.setToDate(to);
        this.setPromotionStatus(status);
    }

    public Promotion addItem(Medicine medicine, Integer discount) {
        this.getPromotionItems().add(new PromotionItem(this, medicine, discount));
        return this;
    }

}

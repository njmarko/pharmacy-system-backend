package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.OfferStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "offer")
@Getter
@Setter
public class Offer extends BaseEntity {

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "delivery_due_date", nullable = false)
    private LocalDateTime deliveryDueDate;

    @Column(name = "status", nullable = false)
    @Enumerated
    private OfferStatus offerStatus;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Supplier supplier;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Order originalOrder;

    public Offer() {
        super();
    }

    public Offer(Double totalPrice, LocalDateTime deliveryDueDate, OfferStatus offerStatus, Supplier supplier, Order order) {
        this();
        this.setTotalPrice(totalPrice);
        this.setDeliveryDueDate(deliveryDueDate);
        this.setOfferStatus(offerStatus);
        this.setSupplier(supplier);
        this.setOriginalOrder(order);
    }
}

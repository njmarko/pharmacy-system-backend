package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "status", nullable = false)
    @Enumerated
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "originalOrder", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Offer> availableOffers = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<MedicineOrderInfo> orderItems = new HashSet<>();

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private PharmacyAdmin pharmacyAdmin;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Pharmacy pharmacy;

    public Order() {
        super();
    }

    public Order(LocalDateTime dueDate, OrderStatus orderStatus, PharmacyAdmin pharmacyAdmin, Pharmacy pharmacy) {
        this.setDueDate(dueDate);
        this.setOrderStatus(orderStatus);
        this.setPharmacyAdmin(pharmacyAdmin);
        this.setPharmacy(pharmacy);
    }
}

package com.mrsisa.pharmacy.domain.valueobjects;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.Order;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class MedicineOrderInfo extends BaseEntity {

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "is_new")
    private Boolean isNew = false;

    @Column(name = "price")
    private Double medicinePrice = null;

    public MedicineOrderInfo() {
        super();
    }

    public MedicineOrderInfo(Integer quantity, Medicine medicine, Boolean isNew, Double price) {
        this(null, quantity, medicine, isNew, price);
    }

    public MedicineOrderInfo(Order order, Integer quantity, Medicine medicine, Boolean isNew, Double price) {
        this();
        this.setOrder(order);
        this.setQuantity(quantity);
        this.setMedicine(medicine);
        this.setIsNew(isNew);
        this.setMedicinePrice(price);
    }

    public MedicineOrderInfo(Integer quantity, Medicine medicine) {
        this(quantity, medicine, false, null);
    }

}

package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stock_price")
@Getter
@Setter
public class StockPrice extends PriceTag {

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private MedicineStock medicineStock;

    public StockPrice() {
        super();
    }

    public StockPrice(Double price, Boolean isPromotion, MedicineStock medicineStock) {
        super(price, isPromotion);
        this.setMedicineStock(medicineStock);
    }

    public StockPrice(Double price, LocalDate from, LocalDate to, Boolean isPromotion, MedicineStock medicineStock) {
        super(price, from, to, isPromotion);
        this.setMedicineStock(medicineStock);
    }

}

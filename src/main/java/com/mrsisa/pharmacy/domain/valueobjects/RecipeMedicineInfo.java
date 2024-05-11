package com.mrsisa.pharmacy.domain.valueobjects;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.Recipe;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "recipe_medicine_info")
@Getter
@Setter
public class RecipeMedicineInfo extends BaseEntity {

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    private Recipe recipe;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "therapy_days", nullable = false)
    private Integer therapyDays;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "price", nullable = false)
    private Double price;

    public RecipeMedicineInfo() {
        super();
    }

    public RecipeMedicineInfo(Integer quantity, Integer therapyDays, Medicine medicine, Double price) {
        this();
        this.setQuantity(quantity);
        this.setTherapyDays(therapyDays);
        this.setMedicine(medicine);
        this.setPrice(price);
    }

    public RecipeMedicineInfo(Recipe recipe, Integer quantity, Integer therapyDays, Medicine medicine, Double price) {
        this(quantity, therapyDays, medicine, price);
        this.setRecipe(recipe);
    }

}

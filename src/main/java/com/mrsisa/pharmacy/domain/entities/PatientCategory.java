package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "patient_category")
@Getter
@Setter
public class PatientCategory extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "points", nullable = false, unique = true)
    private Integer points;

    @Column(name = "discount", nullable = false, unique = true)
    private Integer discount;

    @Column(name = "color", nullable = false, unique = true)
    private String color;

    public PatientCategory() {
        super();
    }

    public PatientCategory(String name, Integer points, Integer discount, String color) {
        this();
        this.setName(name);
        this.setPoints(points);
        this.setDiscount(discount);
        this.setColor(color);
    }
}

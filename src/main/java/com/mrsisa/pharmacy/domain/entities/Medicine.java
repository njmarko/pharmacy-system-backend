package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.MedicineShape;
import com.mrsisa.pharmacy.domain.enums.MedicineType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medicine")
@Getter
@Setter
public class Medicine extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "shape", nullable = false)
    @Enumerated
    private MedicineShape medicineShape;

    @Column(name = "medicine_type", nullable = false)
    @Enumerated
    private MedicineType medicineType;

    @Column(name = "composition", nullable = false)
    private String composition;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "issue_on_recipe", nullable = false)
    private Boolean issueOnRecipe;

    @Column(name = "additional_notes", nullable = false)
    private String additionalNotes;

    @Column(name = "average_grade", nullable = false)
    private Double averageGrade;

    @Column(name = "points", nullable = false)
    private Integer points;

    @ManyToMany(cascade = {}, fetch = FetchType.LAZY)
    @JoinTable(name = "medicine_replacement", joinColumns = @JoinColumn(name = "original_id"), inverseJoinColumns = @JoinColumn(name = "replacement_id"))
    private Set<Medicine> replacements = new HashSet<>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "medicine_review")
    private Set<Review> reviews = new HashSet<>();

    public Medicine() {
        super();
    }

    public Medicine(String code, String name, MedicineShape medicineShape, MedicineType medicineType,
                    String composition, String manufacturer, Boolean issueOnRecipe, String additionalNotes, Integer points) {
        this(code, name, medicineShape, medicineType, composition, manufacturer, issueOnRecipe, additionalNotes, 0.0, points);
    }

    public Medicine(String code, String name, MedicineShape medicineShape, MedicineType medicineType,
                    String composition, String manufacturer, Boolean issueOnRecipe, String additionalNotes, Double averageGrade, Integer points) {
        this();
        this.setCode(code);
        this.setName(name);
        this.setMedicineShape(medicineShape);
        this.setMedicineType(medicineType);
        this.setComposition(composition);
        this.setManufacturer(manufacturer);
        this.setIssueOnRecipe(issueOnRecipe);
        this.setAdditionalNotes(additionalNotes);
        this.setAverageGrade(averageGrade);
        this.setPoints(points);
    }
}

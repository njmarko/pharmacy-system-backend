package com.mrsisa.pharmacy.domain.valueobjects;

import com.mrsisa.pharmacy.domain.entities.Recipe;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Embeddable
@Getter
@Setter
public class Report {

    @Column(name = "diagnostics")
    private String diagnostics;

    @OneToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public Report() {
        super();
    }

    public Report(String diagnostics) {
        this();
        this.setDiagnostics(diagnostics);
    }

    public Report(String diagnostics, Recipe recipe) {
        this(diagnostics);
        this.setRecipe(recipe);
    }
}

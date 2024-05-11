package com.mrsisa.pharmacy.domain.aggregates;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IncomeStatistics {

    String name;
    private List<String> labels = new ArrayList<>();
    private List<Double> values = new ArrayList<>();

    public IncomeStatistics() {
        super();
    }

    public IncomeStatistics(String name) {
        this();
        this.name = name;
    }

    public void addSample(String label, Double value) {
        this.labels.add(label);
        this.values.add(value);
    }

}

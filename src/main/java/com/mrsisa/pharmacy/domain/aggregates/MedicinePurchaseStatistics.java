package com.mrsisa.pharmacy.domain.aggregates;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MedicinePurchaseStatistics {

    String name;
    List<String> labels = new ArrayList<>();
    List<Integer> values = new ArrayList<>();

    public MedicinePurchaseStatistics() {
        super();
    }

    public MedicinePurchaseStatistics(String name) {
        this();
        this.name = name;
    }

    public void addSample(String label, Integer value) {
        labels.add(label);
        values.add(value);
    }
}

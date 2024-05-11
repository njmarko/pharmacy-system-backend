package com.mrsisa.pharmacy.domain.aggregates;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppointmentStatistics {

    String name;
    private List<String> labels = new ArrayList<>();
    private List<Long> values = new ArrayList<>();

    public AppointmentStatistics() {
        super();
    }

    public AppointmentStatistics(String name) {
        this();
        this.name = name;
    }

    public void addSample(String label, Long value) {
        this.labels.add(label);
        this.values.add(value);
    }

}

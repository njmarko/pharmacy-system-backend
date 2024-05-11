package com.mrsisa.pharmacy.domain.valueobjects;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "working_day")
@Getter
@Setter
public class WorkingDay extends BaseEntity {

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    private EmploymentContract employee;

    @Column(name = "day", nullable = false)
    @Enumerated
    private DayOfWeek day;

    @Column(name = "from_hours", nullable = false)
    private LocalTime fromHours;

    @Column(name = "to_hours", nullable = false)
    private LocalTime toHours;

    public WorkingDay() {
        super();
    }

    public WorkingDay(DayOfWeek day, LocalTime fromHours, LocalTime toHours) {
        this();
        this.setDay(day);
        this.setFromHours(fromHours);
        this.setToHours(toHours);
    }

    public WorkingDay(EmploymentContract employee, DayOfWeek day, LocalTime fromHours, LocalTime toHours) {
        this(day, fromHours, toHours);
        this.setEmployee(employee);
    }

}

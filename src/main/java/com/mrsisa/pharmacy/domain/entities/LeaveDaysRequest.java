package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.LeaveDaysRequestStatus;
import com.mrsisa.pharmacy.domain.valueobjects.Rejection;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_days_request")
@Getter
@Setter
public class LeaveDaysRequest extends BaseEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDate from;

    @Column(name = "end_date", nullable = false)
    private LocalDate to;

    @Column(name = "status", nullable = false)
    @Enumerated
    private LeaveDaysRequestStatus leaveDaysRequestStatus;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private PharmacyEmployee employee;

    @Embedded
    private Rejection rejection;

    public LeaveDaysRequest() {
        super();
    }

    public LeaveDaysRequest(LocalDate from, LocalDate to, PharmacyEmployee employee, LeaveDaysRequestStatus leaveDaysRequestStatus) {
        this();
        this.setFrom(from);
        this.setTo(to);
        this.setEmployee(employee);
        this.setLeaveDaysRequestStatus(leaveDaysRequestStatus);
    }

    public LeaveDaysRequest(LocalDate from, LocalDate to, PharmacyEmployee employee, LeaveDaysRequestStatus leaveDaysRequestStatus, Rejection rejection) {
        this(from, to, employee, leaveDaysRequestStatus);
        this.setRejection(rejection);
    }
}

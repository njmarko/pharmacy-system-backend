package com.mrsisa.pharmacy.domain.valueobjects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class Rejection {

    @Column(name = "reason")
    private String reason;

    public Rejection() {
        super();
    }

    public Rejection(String reason) {
        this();
        this.setReason(reason);
    }

}

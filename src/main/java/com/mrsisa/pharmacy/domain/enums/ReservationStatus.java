package com.mrsisa.pharmacy.domain.enums;

public enum ReservationStatus {
    RESERVED(0), PICKED(1), CANCELED(2), EXPIRED(3);
    private final Integer type;

    ReservationStatus(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }


    @Override
    public String toString() {
        switch (type) {
            case 0:
                return "Reserved";
            case 1:
                return "Picked";
            case 2:
                return "Canceled";
            case 3:
                return "Expired";
            default:
                return "";
        }
    }
}

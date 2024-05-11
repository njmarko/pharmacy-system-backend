package com.mrsisa.pharmacy.domain.enums;

public enum AppointmentStatus {
    BOOKED(0), TOOK_PLACE(1), CANCELED(2), AVAILABLE(3), MISSED(4);
    private final Integer type;

    AppointmentStatus(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch (type) {
            case 0:
                return "Booked";
            case 1:
                return "Took place";
            case 2:
                return "Canceled";
            case 3:
                return "Available";
            case 4:
                return "Missed";
            default:
                return "";
        }
    }
}

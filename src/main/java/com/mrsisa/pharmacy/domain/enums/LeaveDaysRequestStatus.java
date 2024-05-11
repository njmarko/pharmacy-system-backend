package com.mrsisa.pharmacy.domain.enums;

public enum LeaveDaysRequestStatus {
    PENDING(0), APPROVED(1), REJECTED(2);
    private final Integer type;

    LeaveDaysRequestStatus(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Pending";
            case 1:
                return "Approved";
            case 2:
                return "Rejected";
            default:
                return "";
        }
    }
}

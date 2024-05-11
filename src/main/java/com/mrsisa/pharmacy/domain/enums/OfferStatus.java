package com.mrsisa.pharmacy.domain.enums;

public enum OfferStatus {
    ACCEPTED(0), PENDING(1), REJECTED(2);
    private final Integer type;

    OfferStatus(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Accepted";
            case 1:
                return "Pending";
            case 2:
                return "Rejected";
            default:
                return "";
        }
    }
}

package com.mrsisa.pharmacy.domain.enums;

public enum OrderStatus {
    WAITING_FOR_OFFERS(0), PROCESSED(1), IN_CREATION(2);
    private final Integer type;

    OrderStatus(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Waiting for offers";
            case 1:
                return "Processed";
            case 2:
                return "In creation";
            default:
                return "";
        }
    }
}

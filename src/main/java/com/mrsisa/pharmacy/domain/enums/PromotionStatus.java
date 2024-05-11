package com.mrsisa.pharmacy.domain.enums;

public enum PromotionStatus {
    ACTIVE(0), EXPIRED(1);
    private final Integer type;

    PromotionStatus(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Active";
            case 1:
                return "Expired";
            default:
                return "";
        }
    }
}

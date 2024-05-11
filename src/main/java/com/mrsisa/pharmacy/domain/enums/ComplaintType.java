package com.mrsisa.pharmacy.domain.enums;

public enum ComplaintType {
    EMPLOYEE(0), PHARMACY(1);
    private final Integer type;

    ComplaintType(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Employee";
            case 1:
                return "Pharmacy";
            default:
                return "";
        }
    }
}

package com.mrsisa.pharmacy.domain.enums;

public enum ReviewType {
    MEDICINE(0), PHARMACY(1), EMPLOYEE(2);
    private final Integer type;

    ReviewType(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }


    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Medicine";
            case 1:
                return "Pharmacy";
            case 2:
                return "Employee";
            default:
                return "";
        }
    }
}

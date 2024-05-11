package com.mrsisa.pharmacy.domain.enums;

public enum EmployeeType {
    PHARMACIST(0), DERMATOLOGIST(1);
    private final Integer type;

    EmployeeType(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Pharmacist";
            case 1:
                return "Dermatologist";
            default:
                return "";
        }
    }
}


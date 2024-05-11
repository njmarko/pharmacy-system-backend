package com.mrsisa.pharmacy.domain.enums;

public enum MedicineType {
    ANTIBIOTIC(0), ANESTHETIC(1), ANTIHISTAMINE(2);
    private final Integer type;

    MedicineType(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }


    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Antibiotic";
            case 1:
                return "Anesthetic";
            case 2:
                return "Antihistamine";
            default:
                return "";
        }
    }
}

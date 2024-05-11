package com.mrsisa.pharmacy.domain.enums;

public enum IncomeReportType {
    PHARMACIST_APPOINTMENT(0), DERMATOLOGIST_APPOINTMENT(1), MEDICINE_SALES(2);
    private final Integer type;

    IncomeReportType(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Pharmacist appointments";
            case 1:
                return "Dermatologist appointments";
            case 2:
                return "Medicine sales";
            default:
                return "";
        }
    }
}

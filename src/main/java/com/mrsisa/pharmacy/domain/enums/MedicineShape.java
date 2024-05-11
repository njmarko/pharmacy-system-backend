package com.mrsisa.pharmacy.domain.enums;

public enum MedicineShape {
    CAPSULE(0), TABLET(1), GREASE(2), PASTE(3), GEL(4), SYRUP(5), SOLUTION(6),
    POWDER(7);
    private final Integer type;

    MedicineShape(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case 0:
                return "Capsule";
            case 1:
                return "Tablet";
            case 2:
                return "Grease";
            case 3:
                return "Paste";
            case 4:
                return "Gel";
            case 5:
                return "Syrup";
            case 6:
                return "Solution";
            case 7:
                return "Powder";
            default:
                return "";
        }
    }
}

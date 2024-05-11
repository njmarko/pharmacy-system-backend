package com.mrsisa.pharmacy.comparators;

import com.mrsisa.pharmacy.dto.pharmacy.PharmacyQRSearchDTO;

import java.util.Comparator;

public class PharmacyQRSearchDTOComparator implements Comparator<PharmacyQRSearchDTO> {
    private final int value;
    private final String criteria;
    public PharmacyQRSearchDTOComparator(String criteria, String direction){
        if(direction.equals("asc"))
            this.value = 1;
        else
            this.value = -1;
        this.criteria = criteria;
    }


    @Override
    public int compare(PharmacyQRSearchDTO o1, PharmacyQRSearchDTO o2) {
        var res = 0;
        if(this.criteria.equals("totalStockPrice"))
            res = o1.getTotalStockPrice().compareTo(o2.getTotalStockPrice());
        if(this.criteria.equals("pharmacyAverageGrade"))
            res = o1.getPharmacyAverageGrade().compareTo(o2.getPharmacyAverageGrade());
        if(this.criteria.equals("pharmacyName"))
            res = o1.getPharmacyName().compareTo(o2.getPharmacyName());
        if(this.criteria.equals("pharmacyAddress"))
            res = o1.getPharmacyAddress().compareTo(o2.getPharmacyAddress());
        return res * this.value;
    }
}

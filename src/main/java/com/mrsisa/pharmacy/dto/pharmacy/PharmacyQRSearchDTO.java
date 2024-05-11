package com.mrsisa.pharmacy.dto.pharmacy;

import com.mrsisa.pharmacy.domain.valueobjects.Address;
import com.mrsisa.pharmacy.dto.stock.MedicineStockQRSearchDTO;

import java.util.ArrayList;
import java.util.List;


public class PharmacyQRSearchDTO {

    private String pharmacyName;
    private Long pharmacyId;
    private String pharmacyAddress;
    private Double pharmacyAverageGrade;
    private Double totalStockPrice;
    private List<MedicineStockQRSearchDTO> medicineStock;

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public Long getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(Long pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getPharmacyAddress() {
        return pharmacyAddress;
    }

    public void setPharmacyAddress(Address address) {
        this.pharmacyAddress = address.getStreet() + " " + address.getStreetNumber() + "," + address.getCity() + ", " + address.getZipCode() + ", " + address.getCountry();
    }

    public Double getPharmacyAverageGrade() {
        return pharmacyAverageGrade;
    }

    public void setPharmacyAverageGrade(Double pharmacyAverageGrade) {
        this.pharmacyAverageGrade = pharmacyAverageGrade;
    }

    public Double getTotalStockPrice() {
        return totalStockPrice;
    }

    public void setTotalStockPrice(Double totalStockPrice) {
        this.totalStockPrice = totalStockPrice;
    }

    public List<MedicineStockQRSearchDTO> getMedicineStock() {
        return medicineStock;
    }

    public void setMedicineStock(List<MedicineStockQRSearchDTO> medicineStock) {
        this.medicineStock = medicineStock;
        this.calculateTotalPrice();
    }

    public PharmacyQRSearchDTO(Long pharmacyId, String pharmacyName, Double pharmacyAverageGrade, List<MedicineStockQRSearchDTO> stock, Address address){
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.pharmacyAverageGrade = pharmacyAverageGrade;
        this.medicineStock = stock;
        this.setPharmacyAddress(address);
        this.totalStockPrice = 0.0;
        this.calculateTotalPrice();
    }

    public PharmacyQRSearchDTO(Long pharmacyId, String pharmacyName, Double pharmacyAverageGrade, Address address){
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.pharmacyAverageGrade = pharmacyAverageGrade;
        this.medicineStock = new ArrayList<>();
        this.totalStockPrice = 0.0;
        this.setPharmacyAddress(address);
    }

    public PharmacyQRSearchDTO(Long pharmacyId, String pharmacyName){
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.totalStockPrice = 0.0;
        this.medicineStock = new ArrayList<>();

    }

    public void calculateTotalPrice(){
        for(MedicineStockQRSearchDTO stock: this.medicineStock){
            this.totalStockPrice += stock.getMedicinePrice() * stock.getQuantity();
        }
    }
}

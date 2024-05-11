package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyQRSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPharmacyService extends IJPAService<Pharmacy> {
    Page<Pharmacy> getAllPharmacies(Pageable pageable);

    Page<Pharmacy> getAllPharmaciesSearchFilter(String name, String locationAddressCity,
                                                Double lowGrade, Double highGrade,
                                                Double latitude, Double longitude, Double distance,
                                                Pageable pageable);

    Pharmacy registerPharmacy(String name, String description, Double latitude, Double longitude, String country, String city, String street, String streetNumber, String zipCode);

    Pharmacy getByIdWithEmployees(Long pharmacyId);

    Pharmacy getByIdWithStocks(Long pharmacyId);

    Pharmacy updateAppointmentPrices(Long pharmacyId, Double pharmacistAppointmentPrice, Double dermatologistAppointmentPrice);

    List<Pharmacy> getPharmacyList();

    List<PharmacyQRSearchDTO> getPharmaciesWhereMedicinesAreAvailable(List<Long> ids, List<Integer> quantities, List<Integer> days, PatientCategory patientCategory);

    Page<Pharmacy> getPharmaciesWithAvailablePharmacistAppointmentsOnSpecifiedDateAndtime(String name, String locationAddressCity,
                                                                                          Double lowGrade, Double highGrade,
                                                                                          Double latitude, Double longitude, Double distance,
                                                                                          String dateTime,
                                                                                          Pageable pageable);

    Complaint fileComplaint(Long pharmacyId, Patient patient, String content);

    void ratePharmacy(Long patientId, Long pharmacyId, Integer rating);

    Review getPatientReviewForPharmacy(Long patientId, Long pharmacyId);

    void subscribe(Pharmacy pharmacy, Patient patient);

    void unsubscribe(Pharmacy pharmacy, Patient patient);

    Pharmacy getPharmacyWithSubscribers(Long pharmacyId);
}

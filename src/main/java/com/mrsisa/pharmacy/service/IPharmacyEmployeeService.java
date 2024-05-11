package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface IPharmacyEmployeeService extends IJPAService<PharmacyEmployee> {
    Page<PharmacyEmployee> getPharmacyPharmacists(Pharmacy pharmacy, String firstName, String lastName, Double gradeLow, Double gradeHigh, Pageable pageable);

    Page<PharmacyEmployee> getPharmacyDermatologists(Pharmacy pharmacy, String firstName, String lastName, Double gradeLow, Double gradeHigh, Pageable pageable);

    PharmacyEmployee registerPharmacist(Pharmacy pharmacy, PharmacyEmployee pharmacyEmployee, Collection<WorkingDay> workingDays);

    PharmacyEmployee hireDermatologist(Pharmacy pharmacy, PharmacyEmployee dermatologist, Collection<WorkingDay> workingDays);

    void firePharmacist(Pharmacy pharmacy, PharmacyEmployee pharmacists);

    void fireDermatologist(Pharmacy pharmacy, PharmacyEmployee dermatologist);

    PharmacyEmployee findByUsername(String username);

    Pharmacy findActivePharmacyOfPharmacist(String username);

    PharmacyEmployee getDermatologistById(Long dermatologistId);

    PharmacyEmployee getPharmacistById(Long pharmacistId);

    PharmacyEmployee getDermatologistByUsername(String username);

    PharmacyEmployee createDermatologist(String firstName, String lastName, String username, String password, String email);

    List<Pharmacy> getPharmacyEmployeePharmacies(Long employeeId);

    Page<PharmacyEmployee> getDermatologists(Long pharmacyId, String firstName, String lastName, Double gradeLow, Double gradeHigh, Pageable pageable);

    Page<PharmacyEmployee> getPharmacists(Long pharmacyId, String firstName, String lastName, Double gradeLow, Double gradeHigh, Pageable pageable);

    void rateEmployee(Long patientId, Long employeeId, EmployeeType employeeType, Integer rating);

    Complaint fileComplaint(Long employeeId, Patient patient, String content);

    Review getPatientReviewForEmployee(Long patientId, Long employeeId, EmployeeType employeeType);

    List<PharmacyEmployee> getAllEmployeesOfType(EmployeeType employeeType);
    
    PharmacyEmployee updateEmployee(Long id, String firstName, String lastName);

    List<EmploymentContract> getAllPharmacyEmployees(Pharmacy pharmacy);
}

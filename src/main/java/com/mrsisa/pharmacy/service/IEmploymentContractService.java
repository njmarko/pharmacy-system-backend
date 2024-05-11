package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;

import java.util.List;

public interface IEmploymentContractService extends IJPAService<EmploymentContract> {
    EmploymentContract getPharmacyEmployee(Long pharmacyId, Long employeeId);
    EmploymentContract getPharmacistContract(Long pharmacistId);
    boolean contractWithPharmacyExists(Long employeeId, Long pharmacyId);
    EmploymentContract getContractWithPharmacy(Long employeeId, Long pharmacyId);
    List<EmploymentContract> getEmployeeContractsList(PharmacyEmployee employee);
}

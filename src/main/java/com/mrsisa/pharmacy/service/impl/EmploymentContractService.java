package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.IEmploymentContractRepository;
import com.mrsisa.pharmacy.service.IEmploymentContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmploymentContractService extends JPAService<EmploymentContract> implements IEmploymentContractService {
    private final IEmploymentContractRepository employmentContractRepository;

    private static final String NOT_FOUND = "Cannot find employee with id: ";

    @Autowired
    public EmploymentContractService(IEmploymentContractRepository employmentContractRepository) {
        this.employmentContractRepository = employmentContractRepository;
    }

    @Override
    protected JpaRepository<EmploymentContract, Long> getEntityRepository() {
        return employmentContractRepository;
    }

    @Override
    public EmploymentContract getPharmacyEmployee(Long pharmacyId, Long employeeId) {
        return employmentContractRepository.getEmployeeContractWithPharmacy(employeeId, pharmacyId).orElseThrow(() -> new NotFoundException(NOT_FOUND + employeeId));
    }

    @Override
    public EmploymentContract getPharmacistContract(Long pharmacistId) {
        return employmentContractRepository.getEmployeeContractForEmployee(pharmacistId, EmployeeType.PHARMACIST).orElseThrow(() -> new NotFoundException(NOT_FOUND + pharmacistId));
    }

    @Override
    public boolean contractWithPharmacyExists(Long employeeId, Long pharmacyId) {
        return employmentContractRepository.getEmployeeContractForPharmacyAndEmployee(pharmacyId, employeeId).isPresent();
    }

    @Override
    public EmploymentContract getContractWithPharmacy(Long employeeId, Long pharmacyId) {
        return employmentContractRepository.getEmployeeContractForPharmacyAndEmployee(pharmacyId, employeeId).orElseThrow(() -> new NotFoundException(NOT_FOUND + employeeId));
    }

    @Override
    public List<EmploymentContract> getEmployeeContractsList(PharmacyEmployee employee) {
        return employmentContractRepository.getEmployeeContractsList(employee.getId());
    }
}

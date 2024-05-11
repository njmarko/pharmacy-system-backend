package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.dto.employee.EmployeeDTO;
import com.mrsisa.pharmacy.service.IEmploymentContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PharmacyEmployeeToEmployeeDTO extends AbstractConverter<PharmacyEmployee, EmployeeDTO> implements IConverter<PharmacyEmployee, EmployeeDTO> {
    private final IEmploymentContractService employmentContractService;

    @Autowired
    public PharmacyEmployeeToEmployeeDTO(IEmploymentContractService employmentContractService) {
        this.employmentContractService = employmentContractService;
    }

    @Override
    public EmployeeDTO convert(@NonNull PharmacyEmployee employee) {
        EmployeeDTO dto = getModelMapper().map(employee, EmployeeDTO.class);
        dto.getPharmacyNames().addAll(employmentContractService.getEmployeeContractsList(employee).stream().map(contract -> contract.getPharmacy().getName()).collect(Collectors.toList()));
        return dto;
    }
}

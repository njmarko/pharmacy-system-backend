package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import com.mrsisa.pharmacy.dto.employee.PharmacyEmployeeDetailsDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class EmploymentContractToPharmacyEmployeeDetailsDTO extends AbstractConverter<EmploymentContract, PharmacyEmployeeDetailsDTO> {
    @Override
    public PharmacyEmployeeDetailsDTO convert(@NonNull EmploymentContract contract) {
        PharmacyEmployeeDetailsDTO dto = getModelMapper().map(contract, PharmacyEmployeeDetailsDTO.class);
        dto.setEmployeeId(contract.getPharmacyEmployee().getId());
        return dto;
    }
}

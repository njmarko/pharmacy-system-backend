package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import com.mrsisa.pharmacy.dto.employee.EmployeeListItemDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class EmploymentContractToEmployeeListItemDTO extends AbstractConverter<EmploymentContract, EmployeeListItemDTO> {
    @Override
    public EmployeeListItemDTO convert(@NonNull EmploymentContract contract) {
        return getModelMapper().map(contract, EmployeeListItemDTO.class);
    }
}

package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.dto.employee.EmployeeListItemDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PharmacyEmployeeToEmployeeListItemDTO extends AbstractConverter<PharmacyEmployee, EmployeeListItemDTO> {
    @Override
    public EmployeeListItemDTO convert(@NonNull PharmacyEmployee pharmacyEmployee) {
        return getModelMapper().map(pharmacyEmployee, EmployeeListItemDTO.class);
    }
}

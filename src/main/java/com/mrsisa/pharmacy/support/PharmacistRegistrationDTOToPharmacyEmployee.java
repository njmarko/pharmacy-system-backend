package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.dto.PharmacistRegistrationDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
public class PharmacistRegistrationDTOToPharmacyEmployee extends AbstractConverter<PharmacistRegistrationDTO, PharmacyEmployee> implements IConverter<PharmacistRegistrationDTO, PharmacyEmployee> {
    @Override
    public PharmacyEmployee convert(@NonNull PharmacistRegistrationDTO pharmacistRegistrationDTO) {
        PharmacyEmployee employee = getModelMapper().map(pharmacistRegistrationDTO, PharmacyEmployee.class);
        employee.setId(null); // Just to make sure ModelMapper doesn't use pharmacyId here
        employee.setEmployeeType(EmployeeType.PHARMACIST);
        employee.setVerified(true);
        employee.setLoggedIn(false);
        employee.setAverageGrade(0.0);
        return employee;
    }
}

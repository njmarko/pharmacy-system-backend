package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import com.mrsisa.pharmacy.dto.PharmacistRegistrationDTO;
import com.mrsisa.pharmacy.dto.WorkingDayDTO;
import com.mrsisa.pharmacy.dto.employee.EmployeeDTO;
import com.mrsisa.pharmacy.dto.employee.EmployeeSearchDTO;
import com.mrsisa.pharmacy.service.IPharmacyAdminService;
import com.mrsisa.pharmacy.service.IPharmacyEmployeeService;
import com.mrsisa.pharmacy.service.IPharmacyService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/pharmacies")
public class PharmacyPharmacistController extends PharmacyControllerBase {
    private final IPharmacyEmployeeService pharmacyEmployeeService;
    private final IConverter<PharmacyEmployee, EmployeeDTO> toEmployeeDTO;
    private final IConverter<PharmacistRegistrationDTO, PharmacyEmployee> toPharmacyEmployee;
    private final IConverter<WorkingDayDTO, WorkingDay> toWorkingDay;

    @Autowired
    public PharmacyPharmacistController(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService, IPharmacyEmployeeService pharmacyEmployeeService, IConverter<PharmacyEmployee, EmployeeDTO> toEmployeeDTO, IConverter<PharmacistRegistrationDTO, PharmacyEmployee> toPharmacyEmployee, IConverter<WorkingDayDTO, WorkingDay> toWorkingDay) {
        super(pharmacyService, pharmacyAdminService);
        this.pharmacyEmployeeService = pharmacyEmployeeService;
        this.toEmployeeDTO = toEmployeeDTO;
        this.toPharmacyEmployee = toPharmacyEmployee;
        this.toWorkingDay = toWorkingDay;
    }

    @GetMapping(value = "/{id}/pharmacists")
    public Page<EmployeeDTO> getPharmacists(@PathVariable("id") Long id, @Valid EmployeeSearchDTO searchDTO, @PageableDefault Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<PharmacyEmployee> employeePage = pharmacyEmployeeService.getPharmacyPharmacists(pharmacy, searchDTO.getFirstName().toLowerCase().trim(),
                searchDTO.getLastName().toLowerCase().trim(), searchDTO.getGradeLow(), searchDTO.getGradeHigh(), pageable);
        return employeePage.map(toEmployeeDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PostMapping(value = "/{id}/pharmacists")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO registerPharmacist(@PathVariable("id") Long id, @Valid @RequestBody PharmacistRegistrationDTO pharmacistRegistrationDTO) {
        var pharmacy = pharmacyService.getByIdWithEmployees(id);
        var pharmacyEmployee = toPharmacyEmployee.convert(pharmacistRegistrationDTO);
        pharmacyEmployee = pharmacyEmployeeService.registerPharmacist(pharmacy, pharmacyEmployee, toWorkingDay.convert(pharmacistRegistrationDTO.getWorkingDays()));
        return toEmployeeDTO.convert(pharmacyEmployee);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "pharmacyId")
    @DeleteMapping(value = "/{id}/pharmacists/{pharmacistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void firePharmacist(@PathVariable("id") Long pharmacyId, @PathVariable("pharmacistId") Long pharmacistId) {
        var pharmacy = getOr404(pharmacyId);
        var pharmacist = pharmacyEmployeeService.getPharmacistById(pharmacistId);
        pharmacyEmployeeService.firePharmacist(pharmacy, pharmacist);
    }
}

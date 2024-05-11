package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwnsEntity;
import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import com.mrsisa.pharmacy.dto.employee.EmployeeDTO;
import com.mrsisa.pharmacy.dto.employee.EmployeeSearchDTO;
import com.mrsisa.pharmacy.dto.HireDermatologistDTO;
import com.mrsisa.pharmacy.dto.WorkingDayDTO;
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
public class PharmacyDermatologistController extends PharmacyControllerBase {
    private final IPharmacyEmployeeService pharmacyEmployeeService;
    private final IConverter<PharmacyEmployee, EmployeeDTO> toEmployeeDTO;
    private final IConverter<WorkingDayDTO, WorkingDay> toWorkingDay;

    @Autowired
    public PharmacyDermatologistController(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService, IPharmacyEmployeeService pharmacyEmployeeService, IConverter<PharmacyEmployee, EmployeeDTO> toEmployeeDTO, IConverter<WorkingDayDTO, WorkingDay> toWorkingDay) {
        super(pharmacyService, pharmacyAdminService);
        this.pharmacyEmployeeService = pharmacyEmployeeService;
        this.toEmployeeDTO = toEmployeeDTO;
        this.toWorkingDay = toWorkingDay;
    }

    @GetMapping(value = "/{id}/dermatologists")
    public Page<EmployeeDTO> getDermatologists(@PathVariable("id") Long id, @Valid EmployeeSearchDTO searchDTO, @PageableDefault Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<PharmacyEmployee> employeePage = pharmacyEmployeeService.getPharmacyDermatologists(pharmacy, searchDTO.getFirstName().toLowerCase().trim(),
                searchDTO.getLastName().toLowerCase().trim(), searchDTO.getGradeLow(), searchDTO.getGradeHigh(), pageable);
        return employeePage.map(toEmployeeDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsEntity(entityId = "id", ownerField = "pharmacyAdmins", entity = Pharmacy.class)
    @PostMapping(value = "/{id}/dermatologists")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO registerDermatologist(@PathVariable("id") Long id, @Valid @RequestBody HireDermatologistDTO hireDermatologistDTO) {
        var pharmacy = pharmacyService.getByIdWithEmployees(id);
        var hiredDermatologist = pharmacyEmployeeService.getDermatologistByUsername(hireDermatologistDTO.getDermatologistUsername());
        hiredDermatologist = pharmacyEmployeeService.hireDermatologist(pharmacy, hiredDermatologist, toWorkingDay.convert(hireDermatologistDTO.getWorkingDays()));
        return toEmployeeDTO.convert(hiredDermatologist);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "pharmacyId")
    @DeleteMapping(value = "/{pharmacyId}/dermatologists/{dermatologistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void fireDermatologist(@PathVariable("pharmacyId") Long pharmacyId, @PathVariable("dermatologistId") Long dermatologistId) {
        var pharmacy = getOr404(pharmacyId);
        var dermatologist = pharmacyEmployeeService.getDermatologistById(dermatologistId);
        pharmacyEmployeeService.fireDermatologist(pharmacy, dermatologist);
    }
}

package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.dto.pharmacyadmin.PharmacyAdminRegistrationDTO;
import com.mrsisa.pharmacy.dto.pharmacyadmin.PharmacyAdminUpdateDTO;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyDTO;
import com.mrsisa.pharmacy.dto.UserDTO;
import com.mrsisa.pharmacy.service.IPharmacyAdminService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value= "/api/pharmacy-admins")
public class PharmacyAdminController {
    private final IPharmacyAdminService pharmacyAdminService;
    private final IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO;

    @Autowired
    public PharmacyAdminController(IPharmacyAdminService pharmacyAdminService, IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO){
        this.pharmacyAdminService = pharmacyAdminService;
        this.toPharmacyDTO = toPharmacyDTO;
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwningUser
    @GetMapping(value = "/{id}/pharmacy")
    public PharmacyDTO getPharmacyForPharmacyAdmin(@PathVariable("id") Long id) {
        var pharmacyAdmin = pharmacyAdminService.get(id);
        return toPharmacyDTO.convert(pharmacyAdmin.getPharmacy());
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerPharmacyAdmin(@Valid @RequestBody PharmacyAdminRegistrationDTO dto){
        var admin = this.pharmacyAdminService.registerPharmacyAdmin(dto.getFirstName(), dto.getLastName(), dto.getUsername(), dto.getPassword(), dto.getEmail(), dto.getPharmacyId());
        return new UserDTO(admin.getUsername(), admin.getEmail(), admin.getFirstName(), admin.getLastName(), admin.getId(), admin.getVerified());
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwningUser
    @PutMapping(value = "/{id}")
    public UserDTO updatePharmacyAdmin(@PathVariable("id") Long id, @Valid @RequestBody PharmacyAdminUpdateDTO dto) {
        var admin = pharmacyAdminService.updateAdmin(id, dto.getFirstName(), dto.getLastName());
        return new UserDTO(admin.getUsername(), admin.getEmail(), admin.getFirstName(), admin.getLastName(), admin.getId(), admin.getVerified());
    }

}
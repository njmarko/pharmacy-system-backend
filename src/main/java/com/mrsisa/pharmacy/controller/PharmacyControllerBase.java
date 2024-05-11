package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.service.IPharmacyAdminService;
import com.mrsisa.pharmacy.service.IPharmacyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import javax.persistence.EntityNotFoundException;

public abstract class PharmacyControllerBase {

    protected IPharmacyService pharmacyService;
    protected IPharmacyAdminService pharmacyAdminService;

    protected PharmacyControllerBase(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService) {
        this.pharmacyService = pharmacyService;
        this.pharmacyAdminService = pharmacyAdminService;
    }

    protected Pharmacy getOr404(Long id) {
        try {
            return pharmacyService.get(id);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

}

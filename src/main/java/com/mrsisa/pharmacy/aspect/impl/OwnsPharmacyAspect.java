package com.mrsisa.pharmacy.aspect.impl;

import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.service.IPharmacyAdminService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class OwnsPharmacyAspect extends OwningAspectBase {

    private final IPharmacyAdminService pharmacyAdminService;

    @Autowired
    public OwnsPharmacyAspect(IPharmacyAdminService pharmacyAdminService) {
        this.pharmacyAdminService = pharmacyAdminService;
    }

    @Before("@annotation(com.mrsisa.pharmacy.aspect.OwnsPharmacy)")
    public void pharmacyAdminOwnsPharmacy(JoinPoint joinPoint) {
        Long pharmacyId = getIdentityParameter(joinPoint, OwnsPharmacy.class);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var pharmacyAdmin = pharmacyAdminService.getByUsername(authentication.getName());
        var pharmacy = pharmacyAdmin.getPharmacy();
        if ((pharmacy == null) || (!pharmacy.getId().equals(pharmacyId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions to access this data.");
        }
    }

}

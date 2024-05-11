package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.PharmacyAdmin;

public interface IPharmacyAdminService extends IJPAService<PharmacyAdmin> {

    PharmacyAdmin getByUsername(String username);

    PharmacyAdmin registerPharmacyAdmin(String firstName, String lastName, String username, String password, String email, Long pharmacyId);

    PharmacyAdmin updateAdmin(Long id, String firstName, String lastName);
}

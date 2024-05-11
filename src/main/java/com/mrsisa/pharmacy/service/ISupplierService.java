package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Supplier;

public interface ISupplierService extends IJPAService<Supplier> {

    Supplier registerSupplier(String firstName, String lastName, String username, String password, String email, String company);

    Supplier updateProfileInfo(Long id, String firstName, String lastName, String company);

    Supplier getSupplier(Long id);
}

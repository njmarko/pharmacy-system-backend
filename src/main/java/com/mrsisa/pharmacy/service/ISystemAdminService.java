package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.SystemAdmin;

public interface ISystemAdminService extends IJPAService<SystemAdmin> {

    SystemAdmin registerSystemAdmin(String firstName, String lastName, String username, String password, String email);
}

package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Authority;

public interface IAuthorityService extends IJPAService<Authority> {

    Authority findAuthority(String name);
}

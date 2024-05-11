package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.Authority;
import com.mrsisa.pharmacy.repository.IAuthorityRepository;
import com.mrsisa.pharmacy.service.IAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService extends JPAService<Authority> implements IAuthorityService {
    private final IAuthorityRepository authorityRepository;

    @Autowired
    public AuthorityService(IAuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    protected JpaRepository<Authority, Long> getEntityRepository() {
        return authorityRepository;
    }

    @Override
    public Authority findAuthority(String name) {
        return this.authorityRepository.findByName(name);
    }
}

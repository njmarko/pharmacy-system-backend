package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.SystemAdmin;
import com.mrsisa.pharmacy.repository.IAuthorityRepository;
import com.mrsisa.pharmacy.repository.ISystemAdminRepository;
import com.mrsisa.pharmacy.repository.IUserRepository;
import com.mrsisa.pharmacy.service.ISystemAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SystemAdminService extends JPAService<SystemAdmin> implements ISystemAdminService {
    private final ISystemAdminRepository systemAdminRepository;
    private final IUserRepository userRepository;
    private final IAuthorityRepository authorityRepository;

    @Autowired
    public SystemAdminService(ISystemAdminRepository systemAdminRepository, IUserRepository userRepository, IAuthorityRepository authorityRepository) {
        this.systemAdminRepository = systemAdminRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    protected JpaRepository<SystemAdmin, Long> getEntityRepository() {
        return systemAdminRepository;
    }

    @Override
    public SystemAdmin registerSystemAdmin(String firstName, String lastName, String username, String password, String email) {
        var s = new SystemAdmin(firstName, lastName, username, password, email, true, false);
        if(this.userRepository.findByUsername(username) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.");
        if(this.userRepository.findByEmail(email).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is taken.");
        s.getAuthorities().add(this.authorityRepository.findByName("ROLE_SYSTEM_ADMIN"));
        this.save(s);
        return s;
    }
}

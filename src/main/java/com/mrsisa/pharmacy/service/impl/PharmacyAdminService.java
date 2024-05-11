package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.entities.PharmacyAdmin;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.IAuthorityRepository;
import com.mrsisa.pharmacy.repository.IPharmacyAdminRepository;
import com.mrsisa.pharmacy.repository.IPharmacyRepository;
import com.mrsisa.pharmacy.repository.IUserRepository;
import com.mrsisa.pharmacy.service.IPharmacyAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PharmacyAdminService extends JPAService<PharmacyAdmin> implements IPharmacyAdminService {
    private final IPharmacyAdminRepository pharmacyAdminRepository;
    private final IPharmacyRepository pharmacyRepository;
    private final IUserRepository userRepository;
    private final IAuthorityRepository authorityRepository;

    @Autowired
    public PharmacyAdminService(IPharmacyAdminRepository pharmacyAdminRepository, IPharmacyRepository pharmacyRepository, IUserRepository userRepository, IAuthorityRepository authorityRepository) {
        this.pharmacyAdminRepository = pharmacyAdminRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    protected JpaRepository<PharmacyAdmin, Long> getEntityRepository() {
        return pharmacyAdminRepository;
    }

    @Override
    public PharmacyAdmin getByUsername(String username) {
        return pharmacyAdminRepository.findByUsernameAndActiveIsTrue(username).orElseThrow(() -> new NotFoundException("Cannot find pharmacy admin with username: " + username));
    }

    @Override
    public PharmacyAdmin registerPharmacyAdmin(String firstName, String lastName, String username, String password, String email, Long pharmacyId) {
        if(this.userRepository.findByUsername(username) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.");
        if(this.userRepository.findByEmail(email).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is taken.");
        Optional<Pharmacy> pharmacy = this.pharmacyRepository.findByIdAndActiveTrue(pharmacyId);
        if(pharmacy.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pharmacy with id " + pharmacyId + " does not exist.");
        var admin = new PharmacyAdmin(firstName, lastName, username, password, email, true, false, pharmacy.get());
        admin.getAuthorities().add(this.authorityRepository.findByName("ROLE_PHARMACY_ADMIN"));
        this.save(admin);
        return admin;
    }

    @Override
    public PharmacyAdmin updateAdmin(Long id, String firstName, String lastName) {
        var admin = get(id);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        return admin;
    }
}

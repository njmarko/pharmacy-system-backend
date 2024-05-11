package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.Supplier;
import com.mrsisa.pharmacy.repository.IAuthorityRepository;
import com.mrsisa.pharmacy.repository.ISupplierRepository;
import com.mrsisa.pharmacy.repository.IUserRepository;
import com.mrsisa.pharmacy.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class SupplierService extends JPAService<Supplier> implements ISupplierService {
    private final ISupplierRepository supplierRepository;
    private final IUserRepository userRepository;
    private final IAuthorityRepository authorityRepository;

    @Autowired
    public SupplierService(ISupplierRepository supplierRepository, IUserRepository userRepository, IAuthorityRepository authorityRepository) {
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    protected JpaRepository<Supplier, Long> getEntityRepository() {
        return supplierRepository;
    }

    @Override
    public Supplier registerSupplier(String firstName, String lastName, String username, String password, String email, String company) {
        if(this.userRepository.findByUsername(username) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.");
        if(this.userRepository.findByEmail(email).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is taken.");
        var s = new Supplier(firstName, lastName, username, password, email, true, false, company);
        s.getAuthorities().add(this.authorityRepository.findByName("ROLE_SUPPLIER"));
        this.save(s);
        return s;


    }

    @Override
    public Supplier updateProfileInfo(Long id, String firstName, String lastName, String company) {

        Optional<Supplier> optionalSupplier = this.supplierRepository.getSupplierByActiveTrueAndId(id);
        if(optionalSupplier.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier with id " + id + " does not exist.");
        var supplier = optionalSupplier.get();

        supplier.setFirstName(firstName);
        supplier.setLastName(lastName);
        supplier.setCompany(company);
        this.update(supplier);
        return supplier;
    }

    @Override
    public Supplier getSupplier(Long id) {
        Optional<Supplier> supplierOptional = this.supplierRepository.getSupplierByActiveTrueAndId(id);
        if(supplierOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier with id " + id + " does not exist.");
        return supplierOptional.get();
    }
}

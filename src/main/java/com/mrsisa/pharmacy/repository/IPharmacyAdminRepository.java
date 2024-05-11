package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.PharmacyAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPharmacyAdminRepository extends JpaRepository<PharmacyAdmin, Long> {

    Optional<PharmacyAdmin> findByUsernameAndActiveIsTrue(String username);

}

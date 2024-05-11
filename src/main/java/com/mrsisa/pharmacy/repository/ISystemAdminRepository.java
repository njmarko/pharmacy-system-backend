package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.SystemAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISystemAdminRepository extends JpaRepository<SystemAdmin, Long> {

    Optional<SystemAdmin> findByIdAndActiveTrue(Long id);
}

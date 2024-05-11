package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISupplierRepository extends JpaRepository<Supplier, Long> {


    Optional<Supplier> getSupplierByActiveTrueAndId(Long id);
}

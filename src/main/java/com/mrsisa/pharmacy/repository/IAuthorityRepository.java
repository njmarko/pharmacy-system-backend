package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);
}

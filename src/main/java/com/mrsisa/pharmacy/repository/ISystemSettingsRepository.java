package com.mrsisa.pharmacy.repository;


import com.mrsisa.pharmacy.domain.valueobjects.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface ISystemSettingsRepository extends JpaRepository<SystemSettings, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ss from SystemSettings ss where ss.active = true and ss.id = :id")
    SystemSettings findByIdForUpdate(@Param("id") Long id);
}

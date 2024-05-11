package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.MissingMedicineLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IMissingMedicineLogRepository extends JpaRepository<MissingMedicineLog, Long> {
    @Query("select log from MissingMedicineLog log where log.appointmentSearched.employee.pharmacy.id=:id" +
            " and ((:name is null) or (lower(log.missingMedicine.name) like concat('%', lower(:name), '%') ))")
    Page<MissingMedicineLog> findAllForPharmacy(@Param("id") Long pharmacyId,
                                                @Param("name") String name,
                                                Pageable pageable);
}

package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface IComplaintRepository extends JpaRepository<Complaint, Long> {

    @Query("select c from Complaint c where c.active = true and c.patient.id = :patientId order by c.datePosted desc")
    Page<Complaint> getComplaintsForPatient(Long patientId, Pageable pageable);

    @Query("select c from Complaint c where c.active = true and c.reply is null order by c.datePosted asc")
    Page<Complaint> getUnansweredComplaints(Pageable pageable);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Complaint c where c.active = true and c.id = :id")
    Optional<Complaint> findByIdAndActiveTrueForUpdate(@Param("id") Long id);

    @Query("select c from Complaint c where c.active = true and c.reply.systemAdmin.id = :id")
    Page<Complaint> getAnsweredComplaintsForAdmin(@Param("id") Long id, Pageable pageable);
}

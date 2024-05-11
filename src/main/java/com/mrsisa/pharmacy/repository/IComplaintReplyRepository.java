package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.ComplaintReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IComplaintReplyRepository extends JpaRepository<ComplaintReply, Long> {
}

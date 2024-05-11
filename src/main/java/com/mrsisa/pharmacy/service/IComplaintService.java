package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Complaint;
import com.mrsisa.pharmacy.domain.entities.ComplaintReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IComplaintService extends IJPAService<Complaint> {

    Page<Complaint> getComplaintsForPatient(Long patientId, Pageable pageable);

    Page<Complaint> getUnansweredComplaints(Pageable pageable);

    ComplaintReply writeReply(Long complaintId, Long adminId, String content);

    Page<Complaint> getAnsweredComplaintsForAdmin(Long adminId, Pageable pageable);
}

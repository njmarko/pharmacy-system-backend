package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.Complaint;
import com.mrsisa.pharmacy.domain.entities.ComplaintReply;
import com.mrsisa.pharmacy.domain.entities.SystemAdmin;
import com.mrsisa.pharmacy.repository.IComplaintRepository;
import com.mrsisa.pharmacy.repository.ISystemAdminRepository;
import com.mrsisa.pharmacy.service.IComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ComplaintService extends JPAService<Complaint> implements IComplaintService {
    private final IComplaintRepository complaintRepository;
    private final ISystemAdminRepository adminRepository;

    @Autowired
    public ComplaintService(IComplaintRepository complaintRepository, ISystemAdminRepository adminRepository) {
        this.complaintRepository = complaintRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    protected JpaRepository<Complaint, Long> getEntityRepository() {
        return complaintRepository;
    }

    @Override
    public Page<Complaint> getComplaintsForPatient(Long patientId, Pageable pageable) {
        return this.complaintRepository.getComplaintsForPatient(patientId, pageable);
    }

    @Override
    public Page<Complaint> getUnansweredComplaints(Pageable pageable) {
        return this.complaintRepository.getUnansweredComplaints(pageable);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = ResponseStatusException.class)
    public ComplaintReply writeReply(Long complaintId, Long adminId, String content) {
        Optional<Complaint> optionalComplaint = this.complaintRepository.findByIdAndActiveTrueForUpdate(complaintId);
        if(optionalComplaint.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint with id " + complaintId + " does not exist.");
        Optional<SystemAdmin> adminOptional = this.adminRepository.findByIdAndActiveTrue(adminId);
        if(adminOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "System admin with id " + adminId + " does not exist.");
        var complaint = optionalComplaint.get();
        if(complaint.getReply() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint with id " + complaint.getId() + " has already been replied to.");

        var admin = adminOptional.get();

        var reply = new ComplaintReply(content, LocalDateTime.now(), admin, complaint);
        complaint.setReply(reply);
        return reply;
    }

    @Override
    public Page<Complaint> getAnsweredComplaintsForAdmin(Long adminId, Pageable pageable) {
        return this.complaintRepository.getAnsweredComplaintsForAdmin(adminId, pageable);
    }
}

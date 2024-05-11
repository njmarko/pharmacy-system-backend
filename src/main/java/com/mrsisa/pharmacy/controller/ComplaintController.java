package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.domain.entities.Complaint;
import com.mrsisa.pharmacy.domain.entities.ComplaintReply;
import com.mrsisa.pharmacy.dto.complaint.ComplaintDTO;
import com.mrsisa.pharmacy.dto.complaint.ComplaintReplyCreationDTO;
import com.mrsisa.pharmacy.dto.complaint.ComplaintReplyDTO;
import com.mrsisa.pharmacy.service.IComplaintService;
import com.mrsisa.pharmacy.service.IEmailService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value= "/api/complaints")
public class ComplaintController {

    private final IComplaintService complaintService;
    private final IConverter<Complaint, ComplaintDTO> toComplaintDTO;
    private final IEmailService emailService;

    @Autowired
    public ComplaintController(IComplaintService complaintService, IConverter<Complaint, ComplaintDTO> toComplaintDTO, IEmailService emailService){
        this.complaintService = complaintService;
        this.toComplaintDTO = toComplaintDTO;
        this.emailService = emailService;
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public Page<ComplaintDTO> getUnansweredComplaints(@PageableDefault Pageable pageable){
        Page<Complaint> complaints = this.complaintService.getUnansweredComplaints(pageable);
        return complaints.map(toComplaintDTO::convert);
    }

    @PostMapping("/{id}/replies")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public ComplaintReplyDTO writeReply(@PathVariable("id") Long complaintId, @Valid @RequestBody ComplaintReplyCreationDTO dto){
        ComplaintReply reply = this.complaintService.writeReply(complaintId, dto.getAdminId(), dto.getContent());
        this.emailService.sendComplaintReplyNotification(reply.getComplaint().getPatient(), reply.getComplaint());
        return new ComplaintReplyDTO(reply.getContent(), reply.getSystemAdmin().getUsername(), reply.getDateReplied());
    }
}
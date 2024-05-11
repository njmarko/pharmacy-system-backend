package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.Complaint;
import com.mrsisa.pharmacy.dto.SysAdminRegistrationDTO;
import com.mrsisa.pharmacy.dto.UserDTO;
import com.mrsisa.pharmacy.dto.complaint.ComplaintDTO;
import com.mrsisa.pharmacy.service.IComplaintService;
import com.mrsisa.pharmacy.service.ISystemAdminService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value= "/api/system-admins")
public class SystemAdminController {

    private final ISystemAdminService systemAdminService;
    private final IComplaintService complaintService;
    private final IConverter<Complaint, ComplaintDTO> toComplaintDTO;

    @Autowired
    public SystemAdminController(ISystemAdminService systemAdminService, IComplaintService complaintService, IConverter<Complaint, ComplaintDTO> toComplaintDTO){
        this.systemAdminService = systemAdminService;
        this.complaintService = complaintService;
        this.toComplaintDTO = toComplaintDTO;
    }



    @PostMapping
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerSystemAdmin(@Valid @RequestBody SysAdminRegistrationDTO dto){
        var s = this.systemAdminService.registerSystemAdmin(dto.getFirstName(), dto.getLastName(), dto.getUsername(), dto.getPassword(), dto.getEmail());
        return new UserDTO(s.getUsername(), s.getEmail(), s.getFirstName(), s.getLastName(), s.getId(), s.getVerified());
    }

    @GetMapping("/{id}/complaints")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @OwningUser
    public Page<ComplaintDTO> getAnsweredComplaintsForAdmin(@PathVariable("id") Long id, @PageableDefault Pageable pageable){
        Page<Complaint> complaints = this.complaintService.getAnsweredComplaintsForAdmin(id, pageable);
        return complaints.map(toComplaintDTO::convert);

    }

}
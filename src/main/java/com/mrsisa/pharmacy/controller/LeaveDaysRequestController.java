package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestCreateDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestResponseDTO;
import com.mrsisa.pharmacy.service.IEmailService;
import com.mrsisa.pharmacy.service.ILeaveDaysRequestService;
import com.mrsisa.pharmacy.support.IConverter;
import com.mrsisa.pharmacy.validation.validator.ILeaveDaysRequestCreateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value= "/api/leave-days-requests")
public class LeaveDaysRequestController {

    private final ILeaveDaysRequestService leaveDaysRequestService;
    private final IConverter<LeaveDaysRequest, LeaveDaysRequestDTO> toLeaveDaysRequestDTO;
    private final IEmailService emailService;
    private final ILeaveDaysRequestCreateValidator leaveDaysRequestCreateValidator;

    @Autowired
    public LeaveDaysRequestController(ILeaveDaysRequestService leaveDaysRequestService,
                                      IConverter<LeaveDaysRequest, LeaveDaysRequestDTO> toLeaveDaysRequestDTO,
                                      IEmailService emailService,
                                      ILeaveDaysRequestCreateValidator leaveDaysRequestCreateValidator){
        this.leaveDaysRequestService = leaveDaysRequestService;
        this.toLeaveDaysRequestDTO = toLeaveDaysRequestDTO;
        this.emailService = emailService;
        this.leaveDaysRequestCreateValidator = leaveDaysRequestCreateValidator;
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @GetMapping
    public Page<LeaveDaysRequestDTO> getDermatologistLeaveDaysRequests(@PageableDefault Pageable pageable) {
        Page<LeaveDaysRequest> requestPage = leaveDaysRequestService.getPendingDermatologistsRequest(pageable);
        return requestPage.map(toLeaveDaysRequestDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public LeaveDaysRequestDTO respondToLeaveRequest(@PathVariable("id") Long id, @Valid @RequestBody LeaveDaysRequestResponseDTO dto) {
        var request = leaveDaysRequestService.respondDermatologistRequest(id, dto.getAccepted(), dto.getRejectionReason());
        emailService.notifyEmployeeAboutLeaveRequestResponse(request);
        return toLeaveDaysRequestDTO.convert(request);
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @OwningUser
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveDaysRequestDTO createLeaveDaysRequest(@PathVariable("id") Long id, @Valid @RequestBody LeaveDaysRequestCreateDTO dto) {
        dto.setEmployeeId(id);

        leaveDaysRequestCreateValidator.isValid(dto);

        var leaveDaysRequest = leaveDaysRequestService.createLeaveDaysRequest(id, dto.getFrom(), dto.getTo());

        return toLeaveDaysRequestDTO.convert(leaveDaysRequest);
    }

}

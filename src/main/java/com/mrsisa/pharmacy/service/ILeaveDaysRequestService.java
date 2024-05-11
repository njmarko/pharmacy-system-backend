package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ILeaveDaysRequestService extends IJPAService<LeaveDaysRequest> {
    Page<LeaveDaysRequest> getPendingPharmacistsRequest(Pharmacy pharmacy, Pageable pageable);
    LeaveDaysRequest respondPharmacistRequest(Pharmacy pharmacy, Long requestId, Boolean accepted, String rejectionReason);
    Page<LeaveDaysRequest> getPendingDermatologistsRequest(Pageable pageable);
    LeaveDaysRequest respondDermatologistRequest(Long id, Boolean accepted, String rejectionReason);
    List<LeaveDaysRequest> getAllPendingLeaveDaysRequestForEmployee(Long employeeId);
    LeaveDaysRequest createLeaveDaysRequest(Long employeeId, LocalDate from, LocalDate to);
    List<LeaveDaysRequest> getAllPendingAndAcceptedLeaveDaysRequestForEmployeeForRange(Long employeeId, LocalDateTime from,
                                                                                       LocalDateTime to);
    List<LeaveDaysRequest> getAllPendingAndAcceptedLeaveDaysRequestForEmployee(Long employeeId);
    void rejectPendingStartedLeaveDaysRequests();
}

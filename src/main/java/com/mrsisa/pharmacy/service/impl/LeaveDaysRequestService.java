package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.enums.LeaveDaysRequestStatus;
import com.mrsisa.pharmacy.domain.valueobjects.Rejection;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.IAppointmentRepository;
import com.mrsisa.pharmacy.repository.IEmploymentContractRepository;
import com.mrsisa.pharmacy.repository.ILeaveDaysRequestRepository;
import com.mrsisa.pharmacy.repository.IPharmacyEmployeeRepository;
import com.mrsisa.pharmacy.service.IEmailService;
import com.mrsisa.pharmacy.service.ILeaveDaysRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveDaysRequestService extends JPAService<LeaveDaysRequest> implements ILeaveDaysRequestService {
    private final ILeaveDaysRequestRepository leaveDaysRequestRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IEmploymentContractRepository employmentContractRepository;
    private final IPharmacyEmployeeRepository pharmacyEmployeeRepository;
    private final IEmailService emailService;

    private final Logger log = LoggerFactory.getLogger(LeaveDaysRequestService.class);

    @Autowired
    public LeaveDaysRequestService(ILeaveDaysRequestRepository leaveDaysRequestRepository,
                                   IAppointmentRepository appointmentRepository,
                                   IEmploymentContractRepository employmentContractRepository,
                                   IPharmacyEmployeeRepository pharmacyEmployeeRepository, IEmailService emailService) {
        this.leaveDaysRequestRepository = leaveDaysRequestRepository;
        this.appointmentRepository = appointmentRepository;
        this.employmentContractRepository = employmentContractRepository;
        this.pharmacyEmployeeRepository = pharmacyEmployeeRepository;
        this.emailService = emailService;
    }

    @Override
    protected JpaRepository<LeaveDaysRequest, Long> getEntityRepository() {
        return leaveDaysRequestRepository;
    }

    @Override
    public Page<LeaveDaysRequest> getPendingPharmacistsRequest(Pharmacy pharmacy, Pageable pageable) {
        return leaveDaysRequestRepository.getPendingPharmacistsRequest(pharmacy.getId(), pageable);
    }

    @Override
    public LeaveDaysRequest respondPharmacistRequest(Pharmacy pharmacy, Long requestId, Boolean accepted, String rejectionReason) {
        log.info("Starting respond pharmacist leave days request...");
        var request = leaveDaysRequestRepository.getForUpdate(requestId).orElseThrow(() -> new NotFoundException("Cannot find request with id: " + requestId));
        employmentContractRepository.getEmployeeContractWithPharmacy(request.getEmployee().getId(), pharmacy.getId(), EmployeeType.PHARMACIST).orElseThrow(() -> new BusinessException("This request for leave does not belong to this pharmacy."));
        if (request.getEmployee().getEmployeeType() != EmployeeType.PHARMACIST) {
            throw new BusinessException("This leave request is not made by pharmacist.");
        }
        return respond(request, accepted, rejectionReason);
    }

    @Override
    public Page<LeaveDaysRequest> getPendingDermatologistsRequest(Pageable pageable) {
        return leaveDaysRequestRepository.getPendingDermatologistsRequests(pageable);
    }

    @Override
    public LeaveDaysRequest respondDermatologistRequest(Long id, Boolean accepted, String rejectionReason) {
        var request = leaveDaysRequestRepository.getForUpdate(id).orElseThrow(() -> new NotFoundException("Cannot find request with id: " + id));
        if (request.getEmployee().getEmployeeType() != EmployeeType.DERMATOLOGIST) {
            throw new BusinessException("This leave request is not made by pharmacist.");
        }
        return respond(request, accepted, rejectionReason);
    }

    @Override
    public List<LeaveDaysRequest> getAllPendingLeaveDaysRequestForEmployee(Long employeeId) {
        return leaveDaysRequestRepository.getAllPendingLeaveDaysRequestForEmployee(employeeId);
    }

    @Override
    public LeaveDaysRequest createLeaveDaysRequest(Long employeeId, LocalDate from, LocalDate to) {
        var pharmacyEmployee = pharmacyEmployeeRepository.findById(employeeId).orElseThrow(
                () -> new BusinessException("Employee with id  " + employeeId + " does not exist"));
        var leaveDaysRequest = new LeaveDaysRequest(from, to, pharmacyEmployee, LeaveDaysRequestStatus.PENDING);
        save(leaveDaysRequest);
        return leaveDaysRequest;
    }

    @Override
    public List<LeaveDaysRequest> getAllPendingAndAcceptedLeaveDaysRequestForEmployeeForRange(Long employeeId, LocalDateTime from, LocalDateTime to) {
        return leaveDaysRequestRepository.getAllPendingAndAcceptedLeaveDaysRequestForEmployeeForRange(employeeId, from.toLocalDate(), to.toLocalDate());
    }

    @Override
    public List<LeaveDaysRequest> getAllPendingAndAcceptedLeaveDaysRequestForEmployee(Long employeeId) {
        return leaveDaysRequestRepository.getAllPendingAndAcceptedLeaveDaysRequestForEmployee(employeeId);
    }

    @Override
    public void rejectPendingStartedLeaveDaysRequests() {
        leaveDaysRequestRepository.getStartedPendingRequestsStream(LeaveDaysRequestStatus.PENDING, LocalDate.now()).forEach(req -> {
            req.setLeaveDaysRequestStatus(LeaveDaysRequestStatus.REJECTED);
            req.setRejection(new Rejection("Your request for leave has expired and thus has been rejected automatically."));
            emailService.notifyEmployeeAboutLeaveRequestResponse(req);
        });
    }

    private LeaveDaysRequest respond(LeaveDaysRequest request, Boolean accepted, String rejectionReason) {
        log.info("Starting respond method...");
        if (request.getFrom().isBefore(LocalDate.now())) {
            throw new BusinessException("Leave request is in the past.");
        }
        if (request.getLeaveDaysRequestStatus() != LeaveDaysRequestStatus.PENDING) {
            throw new BusinessException("Leave request already has response.");
        }
        if (Boolean.TRUE.equals(accepted)) {
            // Check if employee has booked appointments at that time
            appointmentRepository.getAppointmentsForEmployeePerson(request.getEmployee().getId(), request.getFrom().atStartOfDay(), request.getTo().plusDays(1).atStartOfDay())
                    .findAny().ifPresent(appointment -> {
                throw new BusinessException("Employee has appointments created at that time.");
            });
            request.setLeaveDaysRequestStatus(LeaveDaysRequestStatus.APPROVED);
        } else {
            request.setLeaveDaysRequestStatus(LeaveDaysRequestStatus.REJECTED);
            request.setRejection(new Rejection(rejectionReason));
        }
        log.info("Finished respond method...");
        return request;
    }
}

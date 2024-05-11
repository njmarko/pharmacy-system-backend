package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.comparators.PharmacyQRSearchDTOComparator;
import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.dto.complaint.ComplaintCreationDTO;
import com.mrsisa.pharmacy.dto.complaint.ComplaintDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestResponseDTO;
import com.mrsisa.pharmacy.dto.medicine.MissingMedicineDTO;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyDTO;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyQRSearchDTO;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyRegistrationDTO;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacySearchDTO;
import com.mrsisa.pharmacy.dto.promotion.PromotionCreationDTO;
import com.mrsisa.pharmacy.dto.promotion.PromotionCreationResponseDTO;
import com.mrsisa.pharmacy.service.*;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/pharmacies")
public class PharmacyController extends PharmacyControllerBase {
    private final IMissingMedicineLogService missingMedicineLogService;
    private final ILeaveDaysRequestService leaveDaysRequestService;
    private final IEmailService emailService;
    private final IPatientService patientService;
    private final IPromotionService promotionService;
    private final IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO;
    private final IConverter<MissingMedicineLog, MissingMedicineDTO> toMissingMedicineDTO;
    private final IConverter<LeaveDaysRequest, LeaveDaysRequestDTO> toLeaveDaysRequestDTO;
    private final IConverter<Complaint, ComplaintDTO> toComplaintDTO;
    private final IConverter<PromotionCreationDTO, Promotion> toPromotion;
    private final IConverter<Promotion, PromotionCreationResponseDTO> toPromotionResponseDTO;

    @Autowired
    public PharmacyController(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService, IMissingMedicineLogService missingMedicineLogService, ILeaveDaysRequestService leaveDaysRequestService,
                              IEmailService emailService, IPatientService patientService, IPromotionService promotionService, IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO, IConverter<MissingMedicineLog, MissingMedicineDTO> toMissingMedicineDTO,
                              IConverter<LeaveDaysRequest, LeaveDaysRequestDTO> toLeaveDaysRequestDTO, IConverter<Complaint, ComplaintDTO> toComplaintDTO, IConverter<PromotionCreationDTO, Promotion> toPromotion, IConverter<Promotion, PromotionCreationResponseDTO> toPromotionResponseDTO) {
        super(pharmacyService, pharmacyAdminService);
        this.missingMedicineLogService = missingMedicineLogService;
        this.leaveDaysRequestService = leaveDaysRequestService;
        this.emailService = emailService;
        this.patientService = patientService;
        this.promotionService = promotionService;
        this.toPharmacyDTO = toPharmacyDTO;
        this.toMissingMedicineDTO = toMissingMedicineDTO;
        this.toLeaveDaysRequestDTO = toLeaveDaysRequestDTO;
        this.toComplaintDTO = toComplaintDTO;
        this.toPromotion = toPromotion;
        this.toPromotionResponseDTO = toPromotionResponseDTO;
    }

    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_PATIENT')")
    @GetMapping(value = "/all")
    public List<PharmacyDTO> getAllPharmacies() {
        return (List<PharmacyDTO>) toPharmacyDTO.convert(pharmacyService.getPharmacyList());
    }

    @GetMapping(value = "/{id}")
    public PharmacyDTO getPharmacy(@PathVariable("id") Long id) {
        return toPharmacyDTO.convert(getOr404(id));
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PostMapping(value = "/{id}/promotions")
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionCreationResponseDTO createPromotion(@PathVariable("id") Long id, @Valid @RequestBody PromotionCreationDTO dto) {
        var promotion = toPromotion.convert(dto);
        final var created = promotionService.createPromotion(id, promotion);
        var pharmacy = pharmacyService.getPharmacyWithSubscribers(id);
        pharmacy.getPromotionSubscribers().stream().filter(BaseEntity::getActive).forEach(sub -> emailService.notifySubscriberAboutPromotion(sub, created));
        return toPromotionResponseDTO.convert(created);
    }

    @GetMapping
    public Page<PharmacyDTO> getPharmacies(@Valid PharmacySearchDTO searchDTO, @PageableDefault Pageable pageAndSortParams) {
        return pharmacyService.getAllPharmaciesSearchFilter(searchDTO.getName(), searchDTO.getCity(),
                searchDTO.getGradeLow(),
                searchDTO.getGradeHigh(),
                searchDTO.getUserLatitude(), searchDTO.getUserLongitude(), searchDTO.getDistance(), pageAndSortParams)
                .map(toPharmacyDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PutMapping(value = "/{id}")
    public PharmacyDTO update(@PathVariable("id") Long id, @Valid @RequestBody PharmacyRegistrationDTO dto) {
        var pharmacy = getOr404(id);
        pharmacy.setName(dto.getName());
        pharmacy.setDescription(dto.getDescription());
        pharmacy.setLocation(dto.getLocation());
        return toPharmacyDTO.convert(pharmacyService.update(pharmacy));
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/missing-medicines")
    public Page<MissingMedicineDTO> getMissingMedicines(@PathVariable("id") Long id, @RequestParam(value = "name", defaultValue = "") String name, @PageableDefault Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<MissingMedicineLog> missingMedicineLogs = missingMedicineLogService.getAllForPharmacy(pharmacy, name, pageable);
        return missingMedicineLogs.map(toMissingMedicineDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/leave-days-requests")
    public Page<LeaveDaysRequestDTO> getPharmacistLeaveDaysRequest(@PathVariable("id") Long id, Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<LeaveDaysRequest> requestPage = leaveDaysRequestService.getPendingPharmacistsRequest(pharmacy, pageable);
        return requestPage.map(toLeaveDaysRequestDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PutMapping(value = "/{id}/leave-days-requests/{requestId}")
    public LeaveDaysRequestDTO responseToLeaveRequest(@PathVariable("id") Long id, @PathVariable("requestId") Long requestId, @Valid @RequestBody LeaveDaysRequestResponseDTO dto) {
        var pharmacy = getOr404(id);
        var request = leaveDaysRequestService.respondPharmacistRequest(pharmacy, requestId, dto.getAccepted(), dto.getRejectionReason());
        emailService.notifyEmployeeAboutLeaveRequestResponse(request);
        return toLeaveDaysRequestDTO.convert(request);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PharmacyDTO registerPharmacy(@Valid @RequestBody PharmacyRegistrationDTO dto) {
        try {
            var pharmacy = this.pharmacyService.registerPharmacy(dto.getName(), dto.getDescription(), dto.getLocation().getLatitude(),
                    dto.getLocation().getLongitude(), dto.getLocation().getAddress().getCountry(),
                    dto.getLocation().getAddress().getCity(), dto.getLocation().getAddress().getStreet(),
                    dto.getLocation().getAddress().getStreetNumber(), dto.getLocation().getAddress().getZipCode());
            return toPharmacyDTO.convert(pharmacy);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/qr-code-search")
    public Page<PharmacyQRSearchDTO> getPharmaciesWithStock(
            @RequestParam("ids") List<Long> ids, @RequestParam("quantities") List<Integer> quantities, @RequestParam("days") List<Integer> days,
            @RequestParam("patientId") Long patientId,
            @RequestParam("page") Integer pageNumber, @RequestParam("size") Integer size, @RequestParam("sortBy") String sorting) {
        var patient = this.patientService.getPatientByIdAndActive(patientId);
        List<PharmacyQRSearchDTO> l = this.pharmacyService.getPharmaciesWhereMedicinesAreAvailable(ids, quantities, days, patient.getPatientCategory());
        var tokens = sorting.split(",");
        String criteria = tokens[0];
        String direction = tokens[1];
        Pageable pageable = PageRequest.of(pageNumber, size);
        l.sort(new PharmacyQRSearchDTOComparator(criteria, direction));
        return new PageImpl<>(l.stream().skip((long) pageNumber * size).limit(size).collect(Collectors.toList()), pageable, l.size());

    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping("/{id}/complaints")
    public ComplaintDTO fileComplaint(@PathVariable("id") Long pharmacyId, @Valid @RequestBody ComplaintCreationDTO dto) {
        var patient = this.patientService.getPatientByIdAndActive(dto.getPatientId());
        if (patient == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with id " + dto.getPatientId() + " does not exist.");

        var complaint = this.pharmacyService.fileComplaint(pharmacyId, patient, dto.getContent());
        return toComplaintDTO.convert(complaint);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping("/{id}/subscriptions")
    public void subscribe(@PathVariable("id") Long pharmacyId, @RequestBody Map<String, Long> requestMap) {
        var patient = checkPatient(requestMap.get("patientId"));
        var pharmacy = pharmacyService.getPharmacyWithSubscribers(pharmacyId);
        this.pharmacyService.subscribe(pharmacy, patient);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @DeleteMapping(value = "/{pharmacyId}/subscriptions/{patientId}")
    public void unsubscribe(@PathVariable("pharmacyId") Long pharmacyId, @PathVariable("patientId") Long patientId) {
        var patient = checkPatient(patientId);
        var pharmacy = pharmacyService.getPharmacyWithSubscribers(pharmacyId);
        this.pharmacyService.unsubscribe(pharmacy, patient);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser(identifier = "patientId")
    @GetMapping(value = "/{pharmacyId}/subscriptions/{patientId}")
    public boolean checkSubscription(@PathVariable("pharmacyId") Long pharmacyId, @PathVariable("patientId") Long patientId) {
        var pharmacy = pharmacyService.getPharmacyWithSubscribers(pharmacyId);
        return pharmacy.getPromotionSubscribers().stream().anyMatch(patient -> patient.getId().equals(patientId));
    }

    private Patient checkPatient(Long patientId) {
        var patient = this.patientService.getPatientByIdAndActive(patientId);
        if (patient == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient with id " + patientId + " does not exist.");
        return patient;
    }
}

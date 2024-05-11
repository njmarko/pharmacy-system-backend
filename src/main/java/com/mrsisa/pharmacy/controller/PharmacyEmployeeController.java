package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.Complaint;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.dto.*;
import com.mrsisa.pharmacy.dto.complaint.ComplaintCreationDTO;
import com.mrsisa.pharmacy.dto.complaint.ComplaintDTO;
import com.mrsisa.pharmacy.dto.employee.*;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyDTO;
import com.mrsisa.pharmacy.service.IPatientService;
import com.mrsisa.pharmacy.service.IPharmacyEmployeeService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value= "/api/pharmacy-employees")
public class PharmacyEmployeeController {

    private final IPharmacyEmployeeService pharmacyEmployeeService;
    private final IPatientService patientService;
    private final IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO;
    private final IConverter<PharmacyEmployee, EmployeeDTO> toEmployeeDTO;
    private final IConverter<Complaint, ComplaintDTO> toComplaintDTO;
    private final IConverter<PharmacyEmployee, EmployeeListItemDTO> toEmployeeListItemDTO;

    @Autowired
    public PharmacyEmployeeController(IPharmacyEmployeeService pharmacyEmployeeService,
                                      IPatientService patientService, IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO, IConverter<PharmacyEmployee, EmployeeDTO> toEmployeeDTO, IConverter<Complaint, ComplaintDTO> toComplaintDTO, IConverter<PharmacyEmployee, EmployeeListItemDTO> toEmployeeListItemDTO) {
        this.pharmacyEmployeeService = pharmacyEmployeeService;
        this.patientService = patientService;
        this.toPharmacyDTO = toPharmacyDTO;
        this.toEmployeeDTO = toEmployeeDTO;
        this.toComplaintDTO = toComplaintDTO;
        this.toEmployeeListItemDTO = toEmployeeListItemDTO;
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/dermatologists")
    public Page<EmployeeDTO> getDermatologists(@Valid EmployeeSearchDTO employeeSearchDTO, @PageableDefault Pageable pageable) {
        Page<PharmacyEmployee> dermatologistPage = pharmacyEmployeeService.getDermatologists(employeeSearchDTO.getPharmacyId(), employeeSearchDTO.getFirstName(), employeeSearchDTO.getLastName(),
                employeeSearchDTO.getGradeLow(), employeeSearchDTO.getGradeHigh(), pageable);
        return dermatologistPage.map(toEmployeeDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @GetMapping(value = "/dermatologists/all")
    public List<EmployeeListItemDTO> getAllDermatologists() {
        List<PharmacyEmployee> dermatologist = pharmacyEmployeeService.getAllEmployeesOfType(EmployeeType.DERMATOLOGIST);
        return (List<EmployeeListItemDTO>) toEmployeeListItemDTO.convert(dermatologist);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/pharmacists")
    public Page<EmployeeDTO> getPharmacists(@Valid EmployeeSearchDTO employeeSearchDTO, @PageableDefault Pageable pageable) {
        Page<PharmacyEmployee> pharmacistPage = pharmacyEmployeeService.getPharmacists(employeeSearchDTO.getPharmacyId(), employeeSearchDTO.getFirstName(), employeeSearchDTO.getLastName(),
                employeeSearchDTO.getGradeLow(), employeeSearchDTO.getGradeHigh(), pageable);
        return pharmacistPage.map(toEmployeeDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PostMapping(value = "/dermatologist")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerDermatologist(@Valid @RequestBody EmployeeRegistrationDTO dto){
        var employee = this.pharmacyEmployeeService.createDermatologist(dto.getFirstName(), dto.getLastName(), dto.getUsername(), dto.getPassword(), dto.getEmail());
        return new UserDTO(employee.getUsername(), employee.getEmail(), employee.getFirstName(), employee.getLastName(), employee.getId(), employee.getVerified());
    }

    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST, ROLE_DERMATOLOGIST')")
    @GetMapping(value = "/all/{id}")
    @OwningUser
    public List<PharmacyDTO> getAllPharmaciesForEmployee(@PathVariable("id") Long id){
        return (List<PharmacyDTO>) toPharmacyDTO.convert(pharmacyEmployeeService.getPharmacyEmployeePharmacies(id));
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping("/{id}/complaints")
    public ComplaintDTO fileComplaint(@PathVariable("id") Long employeeId, @Valid @RequestBody ComplaintCreationDTO dto){
        var patient = this.patientService.getPatientByIdAndActive(dto.getPatientId());
        if(patient == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with id " + dto.getPatientId() + " does not exist.");
        var complaint = this.pharmacyEmployeeService.fileComplaint(employeeId, patient, dto.getContent());
        return toComplaintDTO.convert(complaint);
    }

    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST, ROLE_DERMATOLOGIST')")
    @OwningUser
    @PutMapping(value = "/{id}")
    public UserDTO updatePharmacyEmployee(@PathVariable("id") Long id, @Valid @RequestBody PharmacyEmployeeUpdateDTO dto) {
        var pharmacyEmployee = pharmacyEmployeeService.updateEmployee(id, dto.getFirstName(), dto.getLastName());
        return new UserDTO(pharmacyEmployee.getUsername(), pharmacyEmployee.getEmail(), pharmacyEmployee.getFirstName(),
                pharmacyEmployee.getLastName(), pharmacyEmployee.getId(), pharmacyEmployee.getVerified());
    }

    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST, ROLE_DERMATOLOGIST')")
    @OwningUser
    @GetMapping(value = "/averageGrade/{id}")
    public Double getAverageGrade(@PathVariable("id") Long id) {
        var pharmacyEmployee = pharmacyEmployeeService.get(id);
        return pharmacyEmployee.getAverageGrade();
    }
}
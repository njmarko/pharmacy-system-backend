package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningPatientNotPenalized;
import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.entities.VerificationToken;
import com.mrsisa.pharmacy.dto.*;
import com.mrsisa.pharmacy.dto.appointment.AppointmentDTO;
import com.mrsisa.pharmacy.dto.appointment.AppointmentRangeResultDTO;
import com.mrsisa.pharmacy.dto.complaint.ComplaintDTO;
import com.mrsisa.pharmacy.dto.medicine.DrugReservationDTO;
import com.mrsisa.pharmacy.dto.medicine.MedicineDetailsDTO;
import com.mrsisa.pharmacy.dto.medicine.MedicineReservationDTO;
import com.mrsisa.pharmacy.dto.patient.*;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyDTO;
import com.mrsisa.pharmacy.dto.recipe.RecipeCreationDTO;
import com.mrsisa.pharmacy.dto.recipe.RecipeDTO;
import com.mrsisa.pharmacy.repository.IVerificationTokenRepository;
import com.mrsisa.pharmacy.service.*;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping(value = "/api/patients")
public class PatientController {

    private final IPatientService patientService;
    private final IPatientCategoryService patientCategoryService;
    private final IConverter<Patient, PatientDTO> toPatientDTO;
    private final IConverter<Patient, PatientSearchResultDTO> toPatientSearchDTO;
    private final IEmailService emailService;
    private final IMedicineReservationService medicineReservationService;
    private final IPharmacyService pharmacyService;
    private final IMedicineService medicineService;
    private final IComplaintService complaintService;
    private final IConverter<MedicineReservation, MedicineReservationDTO> toMedicineReservationDTO;
    private final IConverter<Medicine, MedicineDetailsDTO> toMedicineDetailsDTO;
    private final IConverter<Appointment, AppointmentDTO> toAppointmentDTO;
    private final IAppointmentService appointmentService;
    private final IVerificationTokenRepository verificationTokenRepository;
    private final IRecipeService recipeService;
    private final IConverter<Appointment, AppointmentRangeResultDTO> toAppointmentRangeDTO;
    private final IConverter<Recipe, RecipeDTO> toRecipeDTO;
    private final IConverter<Complaint, ComplaintDTO> toComplaintDTO;
    private final IPharmacyEmployeeService pharmacyEmployeeService;
    private final IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO;

    @Value("${vue.path}")
    private String host;

    public PatientController(IPatientService patientService, IPatientCategoryService patientCategoryService, IConverter<Patient, PatientDTO> toPatientDTO, IConverter<Patient, PatientSearchResultDTO> toPatientSearchDTO, IEmailService emailService, IMedicineReservationService medicineReservationService, IPharmacyService pharmacyService, IMedicineService medicineService, IComplaintService complaintService, IConverter<MedicineReservation, MedicineReservationDTO> toMedicineReservationDTO, IConverter<Medicine, MedicineDetailsDTO> toMedicineDetailsDTO, IConverter<Appointment, AppointmentDTO> toAppointmentDTO, IAppointmentService appointmentService, IVerificationTokenRepository verificationTokenRepository, IRecipeService recipeService, IConverter<Appointment, AppointmentRangeResultDTO> toAppointmentRangeDTO, IConverter<Recipe, RecipeDTO> toRecipeDTO, IConverter<Complaint, ComplaintDTO> toComplaintDTO, IPharmacyEmployeeService pharmacyEmployeeService, IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO) {
        this.patientService = patientService;
        this.patientCategoryService = patientCategoryService;
        this.toPatientDTO = toPatientDTO;
        this.toPatientSearchDTO = toPatientSearchDTO;
        this.emailService = emailService;
        this.medicineReservationService = medicineReservationService;
        this.pharmacyService = pharmacyService;
        this.medicineService = medicineService;
        this.complaintService = complaintService;
        this.toMedicineReservationDTO = toMedicineReservationDTO;
        this.toMedicineDetailsDTO = toMedicineDetailsDTO;
        this.toAppointmentDTO = toAppointmentDTO;
        this.appointmentService = appointmentService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.recipeService = recipeService;
        this.toAppointmentRangeDTO = toAppointmentRangeDTO;
        this.toRecipeDTO = toRecipeDTO;
        this.toComplaintDTO = toComplaintDTO;
        this.pharmacyEmployeeService = pharmacyEmployeeService;
        this.toPharmacyDTO = toPharmacyDTO;
    }


    @PostMapping(value = "/{id}/medicine-reservations")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningPatientNotPenalized(actionName = "drug reservation")
    @ResponseStatus(value = HttpStatus.CREATED, reason = "Drug reserved successfully. Details were sent to your email.")
    public MedicineReservationDTO makeReservation(@PathVariable("id") Long id,
                                                  @Valid @RequestBody DrugReservationDTO dto) {

        MedicineReservation reservation = this.medicineReservationService
                .makePatientDrugReservation(id, dto.getPharmacyId(), dto.getMedicineId(), dto.getQuantity(),
                        dto.getReservedAt(), dto.getReservationDeadline());

        this.emailService.sendDrugReservationCreatedMessage(reservation);

        return toMedicineReservationDTO.convert(reservation);
    }

    @PutMapping(value = "/{id}/dermatologist-appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningPatientNotPenalized(actionName = "scheduling of dermatologist appointment")
    public AppointmentDTO bookDermatologistAppointment(@PathVariable("id") Long id,
                                                       @PathVariable("appointmentId") Long appointmentId) {
        var scheduled = this.appointmentService.bookDermatologistAppointment(id, appointmentId);
        this.emailService.sendDermatologistAppointmentScheduledMessage(scheduled);
        return toAppointmentDTO.convert(scheduled);
    }

    @PutMapping(value = "/{id}/pharmacist-appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningPatientNotPenalized(actionName = "scheduling of pharmacist appointment")
    public AppointmentDTO bookPharmacistAppointment(@PathVariable("id") Long id,
                                                    @PathVariable("appointmentId") Long appointmentId) {
        var scheduled = this.appointmentService.bookPharmacistAppointment(id, appointmentId);
        this.emailService.sendPharmacistAppointmentScheduledMessage(scheduled);
        return toAppointmentDTO.convert(scheduled);
    }

    @DeleteMapping(value = "/{id}/appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    @ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "Appointment canceled successfully")
    public void cancelDermatologistAppointment(@PathVariable("id") Long id,
                                               @PathVariable("appointmentId") Long appointmentId) {
        this.appointmentService.cancelAppointment(id, appointmentId);
    }


    @PutMapping(value = "/{id}/employees/{employeeId}/rating")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    @ResponseStatus(value = HttpStatus.OK, reason = "Your rating for the employee was recorded.")
    public void rateEmployee(@PathVariable("id") Long id,
                             @PathVariable("employeeId") Long employeeId,
                             @Valid @RequestBody RatingDTO dto) {
        this.pharmacyEmployeeService.rateEmployee(id, employeeId, dto.getEmployeeType(), dto.getRating());
    }

    @PutMapping(value = "/{id}/pharmacies/{pharmacyId}/rating")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    @ResponseStatus(value = HttpStatus.OK, reason = "Your rating for the pharmacy was recorded.")
    public void ratePharmacy(@PathVariable("id") Long id,
                             @PathVariable("pharmacyId") Long pharmacyId,
                             @Valid @RequestBody RatingDTO dto) {
        this.pharmacyService.ratePharmacy(id, pharmacyId, dto.getRating());
    }

    @GetMapping(value = "/{id}/pharmacies/{pharmacyId}/rating")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    @ResponseStatus(value = HttpStatus.OK)
    public RatingDTO getPatientRatingForPharmacy(@PathVariable("id") Long id,
                                                 @PathVariable("pharmacyId") Long pharmacyId) {
        var review = this.pharmacyService.getPatientReviewForPharmacy(id, pharmacyId);
        if (review == null) {
            return new RatingDTO();
        }
        return new RatingDTO(review.getGrade(), null);
    }


    @PutMapping(value = "/{id}/drugs/{drugId}/rating")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    @ResponseStatus(value = HttpStatus.OK, reason = "Your rating for the drug was recorded.")
    public void rateDrug(@PathVariable("id") Long id,
                         @PathVariable("drugId") Long drugId,
                         @Valid @RequestBody RatingDTO dto) {
        this.medicineService.rateDrug(id, drugId, dto.getRating());
    }


    @GetMapping(value = "/{id}/past-dermatologist-appointments")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<AppointmentDTO> getPastDermatologistAppointments(@PathVariable("id") Long id,
                                                                 @RequestParam(value = "name", defaultValue = "") String name,
                                                                 @PageableDefault Pageable pageable) {
        return this.appointmentService.getPreviousAppointmentsForPatient(id, EmployeeType.DERMATOLOGIST, name, pageable)
                .map(toAppointmentDTO::convert);
    }

    @GetMapping(value = "/{id}/past-pharmacist-appointments")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<AppointmentDTO> getPastPharmacistAppointments(@PathVariable("id") Long id,
                                                              @RequestParam(value = "name", defaultValue = "") String name,
                                                              @PageableDefault Pageable pageable) {
        return this.appointmentService.getPreviousAppointmentsForPatient(id, EmployeeType.PHARMACIST, name, pageable)
                .map(toAppointmentDTO::convert);
    }

    @GetMapping(value = "/{id}/scheduled-dermatologist-appointments")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<AppointmentDTO> getPatientScheduledDermatologistAppointments(@PathVariable("id") Long id,
                                                                             @RequestParam(value = "name", defaultValue = "") String name,
                                                                             @PageableDefault Pageable pageable) {
        return this.appointmentService.getScheduledAppointments(id, EmployeeType.DERMATOLOGIST, name, pageable)
                .map(toAppointmentDTO::convert);
    }

    @GetMapping(value = "/{id}/scheduled-pharmacist-appointments")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<AppointmentDTO> getPatientScheduledPharmacistAppointments(@PathVariable("id") Long id,
                                                                          @RequestParam(value = "name", defaultValue = "") String name,
                                                                          @PageableDefault Pageable pageable) {
        return this.appointmentService.getScheduledAppointments(id, EmployeeType.PHARMACIST, name, pageable)
                .map(toAppointmentDTO::convert);
    }

    @GetMapping(value = "/{id}/scheduled-dermatologist-appointments/calendar")
    public List<AppointmentRangeResultDTO> getPatientScheduledDermatologistAppointmentsCalendar(
            @PathVariable("id") Long id, @RequestParam("from") String fromTime, @RequestParam("to") String toTime) {
        var patient = getOr404(id);
        List<Appointment> appointmentPage = appointmentService.
                getScheduledAppointmentsForPatient(patient, EmployeeType.DERMATOLOGIST,
                        LocalDateTime.parse(fromTime, DateTimeFormatter.ISO_DATE_TIME),
                        LocalDateTime.parse(toTime, DateTimeFormatter.ISO_DATE_TIME));
        return (List<AppointmentRangeResultDTO>) toAppointmentRangeDTO.convert(appointmentPage);
    }

    @GetMapping(value = "/{id}/scheduled-pharmacist-appointments/calendar")
    public List<AppointmentRangeResultDTO> getPatientScheduledPharmacistAppointmentsCalendar(
            @PathVariable("id") Long id, @RequestParam("from") String fromTime, @RequestParam("to") String toTime) {
        var patient = getOr404(id);
        List<Appointment> appointmentPage = appointmentService.
                getScheduledAppointmentsForPatient(patient, EmployeeType.PHARMACIST,
                        LocalDateTime.parse(fromTime, DateTimeFormatter.ISO_DATE_TIME),
                        LocalDateTime.parse(toTime, DateTimeFormatter.ISO_DATE_TIME));
        return (List<AppointmentRangeResultDTO>) toAppointmentRangeDTO.convert(appointmentPage);
    }


    @GetMapping(value = "/{id}/medicine-reservations")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<MedicineReservationDTO> getReservations(@PathVariable("id") Long id, Long reservationId,
                                                        @PageableDefault Pageable pageable) {
        return this.medicineReservationService.getMedicineReservationsForPatient(id, reservationId, pageable)
                .map(toMedicineReservationDTO::convert);
    }


    @GetMapping(value = "/{id}/issued-medicine-reservations")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<MedicineReservationDTO> getIssuedReservations(@PathVariable("id") Long id, Long reservationId,
                                                              @PageableDefault Pageable pageable) {
        return this.medicineReservationService.getIssuedMedicineReservationsForPatient(id, reservationId, pageable)
                .map(toMedicineReservationDTO::convert);
    }


    @GetMapping(value = "/{id}/allergies")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<MedicineDetailsDTO> getPatientAllergies(@PathVariable("id") Long id,
                                                        @RequestParam(value = "name", defaultValue = "") String name,
                                                        @PageableDefault Pageable pageable) {
        return this.patientService.getPatientAllergies(id, name, pageable).map(toMedicineDetailsDTO::convert);
    }

    @PutMapping(value = "/{id}/allergies/{medicineId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public MedicineDetailsDTO addPatientAllergy(@PathVariable("id") Long id, @PathVariable("medicineId") Long medicineId) {
        var med = this.medicineService.getMedicine(medicineId);
        return toMedicineDetailsDTO.convert(this.patientService.addPatientAllergy(id, med));
    }

    @DeleteMapping(value = "/{id}/allergies/{medicineId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    @ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "Allergy removed successfully")
    public void removePatientAllergy(@PathVariable("id") Long id, @PathVariable("medicineId") Long medicineId) {
        this.patientService.removePatientAllergy(id, medicineId);
    }

    @GetMapping(value = "/{id}/not-allergic-to")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<MedicineDetailsDTO> getMedicinePatientIsNotAllergicTo(@PathVariable("id") Long id,
                                                                      @RequestParam(value = "name", defaultValue = "") String name,
                                                                      @PageableDefault Pageable pageable) {
        return this.patientService.getNotAllergicTo(id, name, pageable).map(toMedicineDetailsDTO::convert);
    }

    @PutMapping(value = "/{id}/medicine-reservations/{reservationId}/cancel")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    @ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "Drug reservation canceled successfully.")
    public void cancelReservation(@PathVariable("id") Long id, @PathVariable("reservationId") Long reservationId) {
        this.medicineReservationService.cancelReservation(reservationId);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public PatientDTO updatePatientProfile(@PathVariable("id") Long id, @RequestBody PatientUpdateDTO dto) {
        return this.toPatientDTO.convert(this.patientService.updatePatientPersonalInfo(id, dto.getFirstName(),
                dto.getLastName(), dto.getPhoneNumber(), dto.getCountry(), dto.getCity(),
                dto.getStreet(), dto.getStreetNumber(), dto.getZipCode()));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public PatientDTO getPatient(@PathVariable("id") Long id) {
        return toPatientDTO.convert(getOr404(id));
    }

    @GetMapping(value = "/{id}/next-category")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public PatientCategory getNextCategoryForPatient(@PathVariable("id") Long id){
        var patient = this.patientService.getPatientByIdAndActive(id);
        if(patient == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with id " + id + " does not exist.");
        return this.patientCategoryService.getNextCategory(patient.getNumPoints());
    }

    @GetMapping(value = "/{id}/current-category")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public PatientCategory getCurrentPatientCategory(@PathVariable("id") Long id){
        var patient = this.patientService.getPatientByIdAndActive(id);
        if(patient == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with id " + id + " does not exist.");
        return patient.getPatientCategory();
    }

    private Patient getOr404(Long id) {
        try {
            return patientService.getPatientByIdAndActive(id);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @GetMapping(value = "/allPatients")
    @PreAuthorize("hasAnyRole('ROLE_DERMATOLOGIST', 'ROLE_PHARMACIST')")
    public Page<PatientSearchResultDTO> getAllPatients(PatientSearchDTO patientSearchDTO,
                                                       @PageableDefault Pageable pageable) {
        Page<Patient> patientPage = patientService.getPatients(patientSearchDTO.getFirstName().toLowerCase().trim(),
                patientSearchDTO.getLastName().toLowerCase().trim(), pageable);
        return patientPage.map(toPatientSearchDTO::convert);
    }

    @GetMapping(value = "/{id}/appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public AppointmentDTO getAppointment(@PathVariable("id") Long id, @PathVariable("appointmentId") Long appointmentId) {
        var a = this.appointmentService.getPatientAppointmentById(appointmentId, id);
        return this.toAppointmentDTO.convert(a);
    }


    @GetMapping(value = "/{id}/reservations/{reservationId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public MedicineReservationDTO getReservation(@PathVariable("id") Long id, @PathVariable("reservationId") Long reservationId) {
        var mr = this.medicineReservationService.getPatientReservationById(reservationId, id);
        return this.toMedicineReservationDTO.convert(mr);
    }

    @PostMapping(value = "/{id}/e-recipe")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningPatientNotPenalized(actionName = "e-recipe drug reservation")
    public RecipeDTO createRecipe(@PathVariable("id") Long id, @RequestBody RecipeCreationDTO dto) {
        var recipe = this.patientService.createRecipe(id, dto.getPharmacyId(), dto.getStocks());
        this.emailService.sendRecipeConfirmationMail(recipe.getPatient(), recipe);
        return toRecipeDTO.convert(recipe);
    }

    @GetMapping(value = "/{id}/e-recipes")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<RecipeDTO> getRecipes(@PathVariable("id") Long id, @PageableDefault Pageable pageable){
        Page<Recipe> page = this.recipeService.getRecipesForPatient(id, pageable);
        return page.map(toRecipeDTO::convert);
    }

    @GetMapping("/{id}/complaints")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<ComplaintDTO> getComplaints(@PathVariable("id") Long id, @PageableDefault Pageable pageable) {
        Page<Complaint> complaints = this.complaintService.getComplaintsForPatient(id, pageable);
        return complaints.map(toComplaintDTO::convert);
    }

    @GetMapping("/{id}/subscriptions")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @OwningUser
    public Page<PharmacyDTO> getSubscriptions(@PathVariable("id") Long id, @PageableDefault Pageable pageable){
        Page<Pharmacy> page = this.patientService.getSubscriptionsForPatient(id, pageable);
        return page.map(toPharmacyDTO::convert);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerPatient(@Valid @RequestBody PatientRegistrationDTO dto) {
        var patient = this.patientService
                .registerPatient(dto.getFirstName(), dto.getLastName(), dto.getUsername(),
                        dto.getPassword(), dto.getEmail(), dto.getPhoneNumber(), dto.getAddress());
        var token = new VerificationToken(patient);
        this.verificationTokenRepository.save(token);

        try {
            this.emailService.sendConfirmationMessage(patient.getUsername(), patient.getEmail(),
                    this.host + "/#/account-activation?token=" + token.getToken());
        } catch (MessagingException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send mail.", exception);
        }
        return new UserDTO(patient.getUsername(), patient.getEmail(), patient.getFirstName(),
                patient.getLastName(), patient.getId(), patient.getVerified());

    }

    @GetMapping(value = "/activate")
    public VerificationDTO activateAccount(@RequestParam("token") String token) {
        var verificationToken = this.verificationTokenRepository.findByToken(token);
        if (verificationToken == null)
            return new VerificationDTO(false, "Activation failed.");
        else {
            var patient = verificationToken.getPatient();
            if (Boolean.TRUE.equals(patient.getVerified()))
                return new VerificationDTO(true, "Account has already been activated.");
            if (verificationToken.isExpired())
                return new VerificationDTO(false, "Activation link has expired.");
            patient.setVerified(true);
            this.patientService.update(patient);
            return new VerificationDTO(true, "Activation successful.");
        }
    }

}

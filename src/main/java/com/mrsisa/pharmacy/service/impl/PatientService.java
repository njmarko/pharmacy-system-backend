package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.ReservationStatus;
import com.mrsisa.pharmacy.domain.valueobjects.Address;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineReservationItem;
import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import com.mrsisa.pharmacy.dto.medicine.MedicineQRCodeReservationItemDTO;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.repository.*;
import com.mrsisa.pharmacy.service.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientService extends JPAService<Patient> implements IPatientService {
    public static final String DOES_NOT_EXIST_ENDING = " does not exist.";
    private final IPatientRepository patientRepository;
    private final IPatientCategoryRepository patientCategoryRepository;
    private final IMedicineRepository medicineRepository;
    private final IAuthorityRepository authorityRepository;
    private final IPatientCategoryRepository categoryRepository;
    private final IUserRepository userRepository;
    private final IPharmacyRepository pharmacyRepository;
    private final IRecipeRepository recipeRepository;
    private final IMedicineStockRepository stockRepository;
    private final IMedicinePurchaseRepository purchaseRepository;
    private final IMedicineReservationRepository medicineReservationRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IVerificationTokenRepository verificationTokenRepository;
    private final IMedicineStockRepository medicineStockRepository;


    @Autowired
    public PatientService(IPatientRepository patientRepository, IPatientCategoryRepository patientCategoryRepository,
                          IMedicineRepository medicineRepository, IAuthorityRepository authorityRepository,
                          IPatientCategoryRepository categoryRepository, IUserRepository userRepository,
                          IPharmacyRepository pharmacyRepository, IRecipeRepository recipeRepository,
                          IMedicineStockRepository stockRepository, IMedicinePurchaseRepository purchaseRepository,
                          IMedicineReservationRepository medicineReservationRepository, IAppointmentRepository appointmentRepository,
                          IVerificationTokenRepository verificationTokenRepository, IMedicineStockRepository medicineStockRepository) {
        this.patientRepository = patientRepository;
        this.patientCategoryRepository = patientCategoryRepository;
        this.medicineRepository = medicineRepository;
        this.authorityRepository = authorityRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.recipeRepository = recipeRepository;
        this.stockRepository = stockRepository;
        this.purchaseRepository = purchaseRepository;
        this.medicineReservationRepository = medicineReservationRepository;
        this.appointmentRepository = appointmentRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.medicineStockRepository = medicineStockRepository;
    }

    @Override
    protected JpaRepository<Patient, Long> getEntityRepository() {
        return patientRepository;
    }


    @Override
    public Patient getPatientByIdAndActive(Long id) {
        return this.patientRepository.findActivePatient(id, true);
    }

    @Override
    public Patient getPatientByIdAndReservations(Long id) {
        return this.patientRepository.getPatientByIdAndReservations(id);
    }

    @Override
    public Patient registerPatient(String firstName, String lastName, String username, String password, String email, String phoneNumber, Address address) {
        Optional<PatientCategory> optionalPatientCategory = this.categoryRepository.findByName("Default category");
        if (optionalPatientCategory.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No default category found. Contact the system administrator.");
        var patient = new Patient(firstName, lastName, username, password, email, false, false, phoneNumber, optionalPatientCategory.get(), address);
        if (this.userRepository.findByUsername(username) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.");
        if (this.userRepository.findByEmail(email).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is taken.");

        patient.getAuthorities().add(this.authorityRepository.findByName("ROLE_PATIENT"));
        this.save(patient);
        return patient;
    }

    @Override
    public Patient getPatientByUsernameAndActive(String username) {
        return this.patientRepository.findByUsernameAndActive(username, true);
    }

    @Override

    public Page<Patient> getPatients(String firstName, String lastName, Pageable pageable) {
        String firstNameParam = "%" + firstName + "%";
        String lastNameParam = "%" + lastName + "%";
        return patientRepository.getPatientsSearch(firstNameParam, lastNameParam, pageable);
    }

    public Patient updatePatientPersonalInfo(Long id, String firstName, String lastName, String phoneNumber,
                                             String country, String city, String street, String streetNumber, String zipCode) {
        Optional<Patient> found = this.patientRepository.findById(id);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the id" + id.toString() + " does not exist and can't be updated!");
        }
        var oldPatient = found.get();

        if (firstName != null && !firstName.isBlank()) {
            oldPatient.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isBlank()) {
            oldPatient.setLastName(lastName);
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            oldPatient.setPhoneNumber(phoneNumber);
        }

        if (country != null && !country.isBlank()) {
            oldPatient.getAddress().setCountry(country);
        }

        if (city != null && !city.isBlank()) {
            oldPatient.getAddress().setCity(city);
        }

        if (street != null && !street.isBlank()) {
            oldPatient.getAddress().setStreet(street);
        }

        if (streetNumber != null && !streetNumber.isBlank()) {
            oldPatient.getAddress().setStreetNumber(streetNumber);
        }

        if (zipCode != null && !zipCode.isBlank()) {
            oldPatient.getAddress().setZipCode(zipCode);
        }

        oldPatient = this.patientRepository.saveAndFlush(oldPatient);

        // one way to avoid lazy fetch issue
        return this.getPatientByIdAndActive(oldPatient.getId());

    }

    @Override
    public Page<Medicine> getPatientAllergies(Long id, String name, Pageable pageable) {
        try {
            return this.medicineRepository.findPatientAllergies(id, name.toLowerCase().trim(), pageable);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bas search or sort parameters.");
        }
    }

    @Override
    public Page<Medicine> getNotAllergicTo(Long id, String name, Pageable pageable) {
        try {
            return this.medicineRepository.findNotAllergicTo(id, name.toLowerCase().trim(), pageable);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bas search or sort parameters.");
        }
    }

    @Override
    public Medicine addPatientAllergy(Long id, Medicine med) {
        if (med == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine does not exist and can't be added to allergies.");
        }
        var patient = this.patientRepository.findActivePatient(id, true);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient does not exist and allergy cant be added.");
        }
        // equals doesn't work so id had to be compared. This is just for the message because set is used so duplicate can't be added.
        if (patient.getAllergicTo().stream().map(Medicine::getId).collect(Collectors.toList()).contains(med.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient already has an allergy to that medicine.");
        }

        patient.getAllergicTo().add(med);
        this.patientRepository.saveAndFlush(patient);
        return med;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = ResponseStatusException.class)
    public Recipe createRecipe(Long patientId, Long pharmacyId, List<MedicineQRCodeReservationItemDTO> medicines) {
        Optional<Pharmacy> optionalPharmacy = this.pharmacyRepository.findByIdAndActiveTrue(pharmacyId);
        if (optionalPharmacy.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pharmacy with id " + pharmacyId + DOES_NOT_EXIST_ENDING);
        var patient = this.patientRepository.findActivePatient(patientId, true);
        if (patient == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with id " + patientId + DOES_NOT_EXIST_ENDING);
        var pharmacy = optionalPharmacy.get();
        var recipe = new Recipe(LocalDateTime.now(), false, patient, pharmacy);
        var price = 0.0;
        for (var item : medicines) {
            var stock = this.stockRepository.getMedicineInPharmacy(pharmacyId, item.getMedicineId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine with id " + item.getMedicineId() + DOES_NOT_EXIST_ENDING));
            Optional<Medicine> optionalMedicine = this.medicineRepository.getMedicineAllergyByMedicineIdAndPatientId(item.getMedicineId(), patientId);
            if (optionalMedicine.isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are allergic to medicine with id " + item.getMedicineId() + " and it cannot be issued via eRecipe.");
            if (item.getQuantity() > stock.getQuantity())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough left in stock for medicine " + stock.getMedicine().getName() + ".");

            patient.addPoints(stock.getMedicine().getPoints() * item.getQuantity());

            stock.setQuantity(stock.getQuantity() - item.getQuantity());
            double discount = (double) (100 - patient.getPatientCategory().getDiscount()) / 100;

            double recipeItemPrice = Math.round(stock.getCurrentPrice() * discount * 100.0) / 100.0;
            price += item.getQuantity() * recipeItemPrice;
            recipe.getReservedMedicines().add(new RecipeMedicineInfo(recipe, item.getQuantity(), item.getTherapyDays(), stock.getMedicine(), recipeItemPrice));
            var purchase = new MedicinePurchase(item.getQuantity(), recipeItemPrice, pharmacy, LocalDate.now(), stock.getMedicine());
            this.purchaseRepository.save(purchase);
        }
        this.update(patient);
        recipe.setPrice(price);
        this.recipeRepository.save(recipe);
        return recipe;
    }

    @Override
    public void removePatientAllergy(Long id, Long medicineId) {
        var patient = this.patientRepository.findActivePatient(id, true);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient does not exist and allergy cant be removed.");
        }
        var found = this.medicineRepository.findByPatientIdAndMedicineId(id, medicineId);

        if (found == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Allergy doesn't exist and can't be removed.");
        }

        patient.getAllergicTo().remove(found);
        this.patientRepository.saveAndFlush(patient);
    }

    @Override
    public void patientNotShowedUp(Long id, Long appointmentId) {
        var patient = patientRepository.findByIdLocked(id)
                .orElseThrow(() -> new BusinessException("Patient with id  " + id + " does not exist"));
        var a = this.appointmentRepository.getAppointmentByIdAndActiveTrue(appointmentId)
                .orElseThrow(() -> new BusinessException("Appointment with id  " + appointmentId + " does not exist"));
        a.setAppointmentStatus(AppointmentStatus.MISSED);
        patient.setNumPenalties(patient.getNumPenalties() + 1);
    }

    @Override
    @Transactional(rollbackFor = ResponseStatusException.class)
    public void removeAllPenalties() {
        patientRepository.findPatientsByActiveTrue().forEach(p -> p.setNumPenalties(0));
    }

    @Override
    public Patient findByUsernameWithAuthorities(String username) {
        return patientRepository.findByUsernameFetchAuthorities(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username: " + username));
    }

    @Override
    @Transactional(rollbackFor = ResponseStatusException.class)
    public void addPenalties() {

        // Add penalties for reservations that were not picked up
        // optimistic lock
        Set<MedicineReservation> expiredReservations =
                this.medicineReservationRepository.getMedicineReservationsBeforeDateTime(
                        ReservationStatus.RESERVED, LocalDateTime.now());
        expiredReservations.parallelStream().forEach(r -> {
            // optimistic lock for patient
            var patient = this.patientRepository.findActivePatientUnlocked(r.getPatient().getId(), true);
            patient.setNumPenalties(patient.getNumPenalties() + 1);

            // optimistic lock for reservation
            r.setReservationStatus(ReservationStatus.EXPIRED);

            // Restore quantity for pharmacy medicine stock
            for (MedicineReservationItem mri :
                    r.getReservedMedicines()) {

                // pessimistic lock for stock
                Optional<MedicineStock> stock = this.medicineStockRepository.getByMedicineCodeForPharmacy(
                        mri.getMedicine().getCode().toLowerCase(), r.getPharmacy().getId());

                stock.ifPresent(medicineStock -> medicineStock.setQuantity(medicineStock.getQuantity() + mri.getQuantity()));

                this.medicineStockRepository.save(stock.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Medicine stock no longer exists!")));
            }

        });
    }

    @Override
    public Page<Pharmacy> getSubscriptionsForPatient(Long id, Pageable pageable) {
        return this.pharmacyRepository.getSubscriptionsForPatient(id, pageable);
    }

    @Override
    public void deleteUnverifiedUsers() {
        List<VerificationToken> verificationTokens = this.verificationTokenRepository.getExpiredTokens();
        for (var token : verificationTokens) {
            Long patientId = token.getPatient().getId();
            Long tokenId = token.getId();
            this.verificationTokenRepository.deleteVerificationTokenById(tokenId);
            this.patientRepository.deleteById(patientId);
        }
    }

    @Override
    public Patient update(Patient patient) {
        List<PatientCategory> categories = this.patientCategoryRepository.getPossibleNewCategory(patient.getNumPoints());
        if (!categories.isEmpty()) {
            PatientCategory category = categories.get(0);
            if (!patient.getPatientCategory().getName().equals(category.getName())) {
                patient.setPatientCategory(category);
            }
        }
        return this.patientRepository.save(patient);
    }

}

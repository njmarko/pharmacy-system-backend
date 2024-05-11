package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.Review;
import com.mrsisa.pharmacy.domain.enums.MedicineShape;
import com.mrsisa.pharmacy.domain.enums.MedicineType;
import com.mrsisa.pharmacy.domain.enums.ReservationStatus;
import com.mrsisa.pharmacy.domain.enums.ReviewType;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.*;
import com.mrsisa.pharmacy.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicineService extends JPAService<Medicine> implements IMedicineService {
    private final IMedicineRepository medicineRepository;
    private final IPatientRepository patientRepository;
    private final IReviewRepository reviewRepository;
    private final IMedicineReservationRepository medicineReservationRepository;
    private final IRecipeRepository recipeRepository;

    @Autowired
    public MedicineService(IMedicineRepository medicineRepository, IPatientRepository patientRepository,
                           IReviewRepository reviewRepository, IMedicineReservationRepository medicineReservationRepository,
                           IRecipeRepository recipeRepository) {
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.reviewRepository = reviewRepository;
        this.medicineReservationRepository = medicineReservationRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    protected JpaRepository<Medicine, Long> getEntityRepository() {
        return medicineRepository;
    }

    @Override
    public Page<Medicine> getAllMedicineSearchAndFilter(String name, Double lowGrade, Double highGrade, Boolean issueOnRecipe, MedicineType type, Pageable pageable) {
        return this.medicineRepository.getMedicineSearchAndFilterWithIssueOnRecipe(name, lowGrade, highGrade, issueOnRecipe, type, pageable);
    }

    @Override
    public Medicine getMedicine(Long id) {
        return this.medicineRepository.findByIdAndActiveTrue(id);
    }

    @Override
    public Medicine registerMedicine(String code, String name, MedicineShape shape, MedicineType type, String composition, String manufacturer, Boolean issueOnRecipe, String additionalNotes, Integer points, List<Long> replacementIds) {

        String notes = additionalNotes == null || additionalNotes.equals("") || additionalNotes.isEmpty() ? "No additional notes." : additionalNotes;

        var medicine = new Medicine(code, name, shape, type, composition, manufacturer, issueOnRecipe, notes, points);
        replacementIds.forEach(id -> {
            var replacement = this.getMedicine(id);
            if (replacement == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine with id " + id + " does not exist.");
            medicine.getReplacements().add(replacement);
        });
        this.save(medicine);
        return medicine;
    }

    @Override
    public List<Medicine> getAllMedicine() {
        return this.medicineRepository.findAllByActiveTrue();
    }

    @Override
    public Medicine getByCode(String medicineCode) {
        return medicineRepository.getByMedicineCode(medicineCode).orElseThrow(() -> new NotFoundException("Cannot find medicine with code: " + medicineCode));
    }

    @Override
    public Page<Medicine> getAllNotInOrder(Long orderId, String name, Double lowGrade, Double highGrade, Boolean issueOnRecipe, MedicineType medicineType, Pageable pageable) {
        return medicineRepository.findAllNotInOrder(orderId, name, lowGrade, highGrade, issueOnRecipe, medicineType, pageable);
    }

    @Override
    public void rateDrug(Long patientId, Long drugId, Integer rating) {
        var patient = this.patientRepository.findActivePatient(patientId, Boolean.TRUE);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + patientId + " does not exist.");
        }
        Medicine drug = this.medicineRepository.getByIdAndActiveTrue(drugId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Medicine does not exist."));

        // check if patient had appointment with any pharmacy employee or if he picked up any medicine from reservation
        // or from the e recipe

        if (this.medicineReservationRepository.checkIfPatientHasMedicineReservationsWithSpecificDrug(patientId,
                drugId, ReservationStatus.PICKED) <= 0 && this.recipeRepository.checkIfPatientHasAnyERecipesWithSpecificDrug(patientId, drugId) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient does not have any " +
                        "prior drug reservation pickups or e-recipe reservations." +
                        " Therefore he cannot rate the drug.");
        }

        // in case user has already reviewed the drug
        var review = drug.getReviews().stream().filter(r -> r.getReviewer().getId().equals(patient.getId())).findFirst()
                // in case there is no existing review for the drug
                .orElse(new Review());

        review.setReviewer(patient);
        review.setGrade(rating);
        review.setReviewType(ReviewType.PHARMACY);
        review.setDatePosted(LocalDate.now());

        drug.getReviews().add(review);
        drug.setAverageGrade(drug.getReviews().parallelStream()
                .reduce(
                        0d, (accumRating, rev) -> accumRating + rev.getGrade(),
                        Double::sum) / drug.getReviews().size());

        this.reviewRepository.save(review);
    }
}

package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.*;
import com.mrsisa.pharmacy.domain.valueobjects.Address;
import com.mrsisa.pharmacy.domain.valueobjects.Location;
import com.mrsisa.pharmacy.dto.stock.MedicineStockQRSearchDTO;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyQRSearchDTO;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.*;
import com.mrsisa.pharmacy.service.IPharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PharmacyService extends JPAService<Pharmacy> implements IPharmacyService {
    public static final String CANNOT_FIND_PHARMACY_WITH_ID_MSG = "Cannot find pharmacy with id: ";
    private final IPharmacyRepository pharmacyRepository;
    private final IAppointmentPriceRepository appointmentPriceRepository;
    private final IMedicineStockRepository medicineStockRepository;
    private final IPatientRepository patientRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IMedicineReservationRepository medicineReservationRepository;
    private final IRecipeRepository recipeRepository;
    private final IReviewRepository reviewRepository;

    @Autowired
    public PharmacyService(IPharmacyRepository pharmacyRepository, IAppointmentPriceRepository appointmentPriceRepository,
                           IMedicineStockRepository medicineStockRepository, IPatientRepository patientRepository,
                           IAppointmentRepository appointmentRepository,
                           IMedicineReservationRepository medicineReservationRepository, IRecipeRepository recipeRepository,
                           IReviewRepository reviewRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.appointmentPriceRepository = appointmentPriceRepository;
        this.medicineStockRepository = medicineStockRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicineReservationRepository = medicineReservationRepository;
        this.recipeRepository = recipeRepository;
        this.reviewRepository = reviewRepository;
    }


    @Override
    protected JpaRepository<Pharmacy, Long> getEntityRepository() {
        return pharmacyRepository;
    }

    @Override
    public Page<Pharmacy> getAllPharmacies(Pageable pageable) {
        return this.pharmacyRepository.findAllByActive(true, pageable);
    }

    @Override
    public Page<Pharmacy> getAllPharmaciesSearchFilter(String name, String locationAddressCity, Double lowGrade,
                                                       Double highGrade, Double latitude, Double longitude,
                                                       Double distance, Pageable pageable) {
        try {
            if (name != null && name.trim().equals("")) {
                name = null;
            }
            if (locationAddressCity != null && locationAddressCity.trim().equals("")) {
                locationAddressCity = null;
            }
            if (name == null && locationAddressCity == null) {
                name = "";
                locationAddressCity = "";
            }
            String nameParam = "%" + (name != null ? name.trim().toLowerCase() : null) + "%";
            String cityParam = "%" + (locationAddressCity != null ? locationAddressCity.trim().toLowerCase() : null) + "%";
            Double lowGradeParam = lowGrade != null ? lowGrade : 0d;
            Double highGradeParam = highGrade != null ? highGrade : 5d;
            if (latitude == null || longitude == null || distance == null) {
                latitude = 0.0d;
                longitude = 0.0d;
                distance = 99999999.0d;
            }
            return pharmacyRepository.getPharmaciesSearchFilter(nameParam, cityParam,
                    lowGradeParam, highGradeParam, latitude, longitude, distance, pageable);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad sort parameters.");
        }
    }

    @Override
    public Pharmacy registerPharmacy(String name, String description, Double latitude, Double longitude,
                                     String country, String city, String street, String streetNumber, String zipCode) {
        var location = new Location(latitude, longitude, new Address(country, city, street, streetNumber, zipCode));
        var pharmacy = new Pharmacy(name, description, location);
        this.save(pharmacy);
        return pharmacy;
    }

    @Override
    public Pharmacy getByIdWithEmployees(Long pharmacyId) {
        return pharmacyRepository.findByIdWithEmployees(pharmacyId)
                .orElseThrow(() -> new BusinessException(CANNOT_FIND_PHARMACY_WITH_ID_MSG + pharmacyId));
    }

    @Override
    public Pharmacy getByIdWithStocks(Long pharmacyId) {
        return pharmacyRepository.findByIdWithStocks(pharmacyId)
                .orElseThrow(() -> new NotFoundException(CANNOT_FIND_PHARMACY_WITH_ID_MSG + pharmacyId));
    }

    @Override

    public Pharmacy updateAppointmentPrices(Long pharmacyId, Double pharmacistAppointmentPrice,
                                            Double dermatologistAppointmentPrice) {
        var pharmacy = get(pharmacyId);
        if (pharmacistAppointmentPrice != null) {
            updatePharmacistAppointmentPrice(pharmacy, pharmacistAppointmentPrice);
        }
        if (dermatologistAppointmentPrice != null) {
            updateDermatologistAppointmentPrice(pharmacy, dermatologistAppointmentPrice);
        }
        save(pharmacy);
        return pharmacy;
    }

    @Override
    public List<Pharmacy> getPharmacyList() {
        return this.pharmacyRepository.findAllByActiveTrue();
    }

    @Override
    public List<PharmacyQRSearchDTO> getPharmaciesWhereMedicinesAreAvailable(List<Long> ids, List<Integer> quantities, List<Integer> days, PatientCategory patientCategory) {
        if (ids.size() != quantities.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sizes of ids and quantities lists do not match.");
        if(ids.size() != days.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sizes of ids and days lists do not match.");
        Set<Long> pharmacyIds = new HashSet<>();
        Map<Long, PharmacyQRSearchDTO> map = new HashMap<>();
        List<MedicineStock> allStocks = new ArrayList<>();

        for (var i = 0; i < ids.size(); i++) {
            Set<Long> tempPharmacyIds = new HashSet<>();
            List<MedicineStock> stocks = this.medicineStockRepository.getAllStocksForMedicine(ids.get(i), quantities.get(i));
            allStocks.addAll(stocks);
            for (var stock : stocks) {
                Long pharmacyId = stock.getPharmacy().getId();
                tempPharmacyIds.add(pharmacyId);
                if (!map.containsKey(pharmacyId))
                    map.put(pharmacyId, new PharmacyQRSearchDTO(pharmacyId, stock.getPharmacy().getName(),
                            stock.getPharmacy().getAverageGrade(), stock.getPharmacy().getLocation().getAddress()));
            }
            if (tempPharmacyIds.isEmpty())
                return new ArrayList<>();
            //ako nije prazan set radim presjek
            if (!pharmacyIds.isEmpty())
                pharmacyIds.retainAll(tempPharmacyIds);
                //ako jeste prazan, samo dodajem sve
            else
                pharmacyIds.addAll(tempPharmacyIds);
        }
        //micem stock ciji id apoteke nije u setu
        allStocks = allStocks.stream().filter(medicineStock -> pharmacyIds.contains(medicineStock.getPharmacy()
                .getId())).collect(Collectors.toList());

        double discount = (double) (100 - patientCategory.getDiscount()) / 100;
        allStocks.forEach(stock -> {
            var pharmacy = stock.getPharmacy();
            int index = ids.indexOf(stock.getMedicine().getId());
            map.get(pharmacy.getId()).getMedicineStock()
                    .add(new MedicineStockQRSearchDTO(stock.getMedicine()
                            .getId(), stock.getCurrentPrice() * discount, stock.getMedicine().getName(), quantities.get(index), days.get(index)));
        });

        Map<Long, PharmacyQRSearchDTO> filtered = map.entrySet()
                .stream()
                .filter(item -> pharmacyIds.contains(item.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        pharmacyIds.forEach(id -> map.get(id).calculateTotalPrice());


        return new ArrayList<>(filtered.values());
    }

    @Override
    public Page<Pharmacy> getPharmaciesWithAvailablePharmacistAppointmentsOnSpecifiedDateAndtime(String name, String locationAddressCity,
                                                                                                 Double lowGrade, Double highGrade,
                                                                                                 Double latitude, Double longitude,
                                                                                                 Double distance, String dateTime,
                                                                                                 Pageable pageable) {
        LocalDateTime dateTimeParam;
        try {
            dateTimeParam = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date and time for the pharmacist appointment must be specified.");
        }
        try {
            if (name != null && name.trim().equals("")) {
                name = null;
            }
            if (locationAddressCity != null && locationAddressCity.trim().equals("")) {
                locationAddressCity = null;
            }
            if (name == null && locationAddressCity == null) {
                name = "";
                locationAddressCity = "";
            }
            String nameParam = "%" + (name != null ? name.trim().toLowerCase() : null) + "%";
            String cityParam = "%" + (locationAddressCity != null ? locationAddressCity.trim().toLowerCase() : null) + "%";
            Double lowGradeParam = lowGrade != null ? lowGrade : 0d;
            Double highGradeParam = highGrade != null ? highGrade : 5d;
            if (latitude == null || longitude == null || distance == null) {
                latitude = 0.0d;
                longitude = 0.0d;
                distance = 99999999.0d;
            }

            return pharmacyRepository.getPharmaciesWithAvailablePharmacistAppointmentsOnSpecifiedDateAndtime(nameParam, cityParam,
                    lowGradeParam, highGradeParam, latitude, longitude, distance, dateTimeParam, EmployeeType.PHARMACIST,
                    AppointmentStatus.AVAILABLE, pageable);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad sort parameters.");
        }
    }

    @Override
    @Transactional(rollbackFor = ResponseStatusException.class)
    public void ratePharmacy(Long patientId, Long pharmacyId, Integer rating) {
        var patient = this.patientRepository.findActivePatientUnlocked(patientId, Boolean.TRUE);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + patientId + " does not exist.");
        }
        var pharmacy = this.pharmacyRepository.findByIdAndActiveTrueUnlocked(pharmacyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Pharmacy does not exist."));

        // check if patient had appointment with any pharmacy employee or if he picked up any medicine from reservation
        // or from the e recipe
        if (this.appointmentRepository
                .checkIfPatientHadAppointmentWithEmployeeFromPharmacy(patientId, pharmacyId, AppointmentStatus.TOOK_PLACE) <= 0) {
            if (this.medicineReservationRepository.checkIfPatientHasMedicineReservationsInPharmacy(patientId,
                    pharmacyId, ReservationStatus.PICKED) <= 0) {
                if (this.recipeRepository.checkIfPatientHasAnyERecipesInPharmacy(patientId, pharmacyId) <= 0)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient does not have any previously concluded" +
                            " appointments with the employee from the " + pharmacy.getName() +
                            " pharmacy, as well as no prior drug reservation pickups or e-recipe reservations." +
                            " Therefore he cannot rate the pharmacy.");
            }

        }

        // in case user has already reviewed the pharmacy
        var review = pharmacy.getReviews().stream().filter(r -> r.getReviewer().getId().equals(patient.getId())).findFirst()
                // in case there is no existing review for the pharmacy
                .orElse(new Review());

        review.setReviewer(patient);
        review.setGrade(rating);
        review.setReviewType(ReviewType.PHARMACY);
        review.setDatePosted(LocalDate.now());

        pharmacy.getReviews().add(review);
        pharmacy.setAverageGrade(pharmacy.getReviews().parallelStream()
                .reduce(
                        0d, (accumRating, rev) -> accumRating + rev.getGrade(),
                        Double::sum) / pharmacy.getReviews().size());

        this.reviewRepository.save(review);
    }

    @Override
    public Review getPatientReviewForPharmacy(Long patientId, Long pharmacyId) {
        var patient = this.patientRepository.findActivePatient(patientId, Boolean.TRUE);
        if (patient == null) {
            return null;
        }
        var pharmacy = this.pharmacyRepository.findByIdAndActiveTrue(pharmacyId)
                .orElse(null);
        if (pharmacy == null) {
            return null;
        }

        // in case user has already reviewed the drug
        return pharmacy.getReviews().stream().filter(r -> r.getReviewer().getId().equals(patient.getId())).findFirst()
                .orElse(null);
    }

    @Override
    public void subscribe(Pharmacy pharmacy, Patient patient) {
        if(pharmacy.getPromotionSubscribers().stream().anyMatch(patient1 -> patient1.getId().equals(patient.getId())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with id " + patient.getId() + " is already subscribed to news about pharmacy with id " + pharmacy.getId() + ".");
        pharmacy.getPromotionSubscribers().add(patient);
        this.pharmacyRepository.save(pharmacy);
    }

    @Override
    public void unsubscribe(Pharmacy pharmacy, Patient patient) {
        if (pharmacy.getPromotionSubscribers().stream().noneMatch(patient1 -> patient1.getId().equals(patient.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with id " + patient.getId() + " is not subscribed to news about pharmacy with id " + pharmacy.getId() + ".");
        }
        pharmacy.setPromotionSubscribers(pharmacy.getPromotionSubscribers().stream().filter(patient1 -> !patient1.getId().equals(patient.getId())).collect(Collectors.toSet()));
        this.pharmacyRepository.save(pharmacy);

    }

    @Override
    public Pharmacy getPharmacyWithSubscribers(Long pharmacyId) {
        return pharmacyRepository.findOneWithSubscribers(pharmacyId).orElseThrow(() -> new NotFoundException(CANNOT_FIND_PHARMACY_WITH_ID_MSG + pharmacyId));
    }

    public Complaint fileComplaint(Long pharmacyId, Patient patient, String content) {
        Optional<Pharmacy> optionalPharmacy = this.pharmacyRepository.getPharmacyWithComplaints(pharmacyId);
        if (optionalPharmacy.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pharmacy with id " + pharmacyId + " does not exist.");
        var pharmacy = optionalPharmacy.get();
        Long appointments = this.appointmentRepository.checkIfPatientHadAppointmentWithEmployeeFromPharmacy(patient.getId(), pharmacyId, AppointmentStatus.TOOK_PLACE);
        Long reservations = this.medicineReservationRepository.checkIfPatientHasMedicineReservationsInPharmacy(patient.getId(), pharmacyId, ReservationStatus.PICKED);
        Long recipes = this.recipeRepository.checkIfPatientHasAnyERecipesInPharmacy(patient.getId(), pharmacyId);

        if (appointments == 0 && reservations == 0 && recipes == 0)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot file a complaint against this pharmacy.");
        var complaint = new Complaint(content, LocalDateTime.now(), ComplaintType.PHARMACY, patient, pharmacy.getName());
        pharmacy.getComplaints().add(complaint);
        this.pharmacyRepository.save(pharmacy);
        return complaint;
    }


    private void updateDermatologistAppointmentPrice(Pharmacy pharmacy, Double dermatologistAppointmentPrice) {
        appointmentPriceRepository.findActiveAppointmentPriceOfType(pharmacy.getId(),
                EmployeeType.DERMATOLOGIST).ifPresent(PriceTag::deprecate);
        pharmacy.addDermatologistAppointmentPrice(
                new AppointmentPrice(dermatologistAppointmentPrice, pharmacy, EmployeeType.DERMATOLOGIST));
    }

    private void updatePharmacistAppointmentPrice(Pharmacy pharmacy, Double pharmacistAppointmentPrice) {
        appointmentPriceRepository.findActiveAppointmentPriceOfType(pharmacy.getId(),
                EmployeeType.PHARMACIST).ifPresent(PriceTag::deprecate);
        pharmacy.addPharmacistAppointmentPrice(
                new AppointmentPrice(pharmacistAppointmentPrice, pharmacy, EmployeeType.PHARMACIST));


    }
}

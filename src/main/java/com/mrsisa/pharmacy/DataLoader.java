package com.mrsisa.pharmacy;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.*;
import com.mrsisa.pharmacy.domain.valueobjects.*;
import com.mrsisa.pharmacy.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Component
public class DataLoader implements ApplicationRunner {
    public static final String FROM_WORKING_HOURS = "09:00";
    public static final String FROM_EMPLOYMENT_CONTRACT = "22.03.2021.";
    public static final String FROM_EMPLOYMENT_CONTRACT_BEFORE = "21.03.2021.";
    public static final String TO_WORKING_HOURS = "17:00";
    public static final String GALENIKA_AD = "GALENIKA AD";
    public static final String SASTAV = "sastav";
    public static final String PROIZVODJAC = "proizvodjac";
    public static final String TEST_123 = "test123";
    public static final String ADMIN = "admin";
    private static  final String DESCRIPTION = "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.";
    private static final String DATE_1 = "06.06.2021. 19:00";
    private static final String DATE_2 = "23.06.2021. 12:30";
    private static final String DATE_3 = "23.06.2021. 09:40";
    private static final String DATE_4 = "28.03.2020. 10:20";
    private static final String DATE_5 = "04.06.2021. 17:00";
    private static final String DATE_6 = "23.06.2021. 15:30";
    private static final String DATE_7 = "21.06.2021. 09:40";
    private static final String DATE_8 = "21.06.2021. 12:30";

    private static final String DIJAGNOSTIKA = "dijagnostika";
    private static final String NOVI_SAD = "Novi Sad";
    private static final String SERBIA = "Serbia";
    private static final String BULEVAR_OSLOBODJENJA = "Bulevar Oslobodjenja";
    private static final String ZIP_CODE = "21101";
    private static final String VESNA = "Vesna";
    private static final String ANDREA = "Andrea";
    private static final String FORMAT = "dd.MM.yyyy. HH:mm";
    public static final String DATE_9 = "21.06.2021. 15:30";
    public static final String DATE_10 = "22.06.2021. 09:40";
    public static final String DATE_11 = "22.06.2021. 12:30";
    public static final String NO_ADDITIONAL_NOTES_MSG = "No additional notes.";
    private final IAuthorityRepository authorityRepository;
    private final IUserRepository userRepository;
    private final IPharmacyRepository pharmacyRepository;
    private final IEmploymentContractRepository employmentContractRepository;
    private final IPatientCategoryRepository patientCategoryRepository;
    private final IMedicineRepository medicineRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IAppointmentPriceRepository appointmentPriceRepository;
    private final IOrderRepository orderRepository;
    private final IOfferRepository offerRepository;
    private final IMedicineReservationRepository medicineReservationRepository;
    private final IMedicineStockRepository medicineStockRepository;
    private final IMissingMedicineLogRepository missingMedicineLogRepository;
    private final ILeaveDaysRequestRepository leaveDaysRequestRepository;
    private final IReviewRepository reviewRepository;
    private final IMedicinePurchaseRepository medicinePurchaseRepository;
    private final IRecipeRepository recipeRepository;
    private final IPromotionRepository promotionRepository;
    private final ISystemSettingsRepository systemSettingsRepository;

    private final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    public DataLoader(IAuthorityRepository authorityRepository, IUserRepository userRepository, IPharmacyRepository pharmacyRepository, IEmploymentContractRepository employmentContractRepository, IPatientCategoryRepository patientCategoryRepository, IMedicineRepository medicineRepository, IAppointmentRepository appointmentRepository, IAppointmentPriceRepository appointmentPriceRepository, IOrderRepository orderRepository, IOfferRepository offerRepository, IMedicineReservationRepository medicineReservationRepository, IMedicineStockRepository medicineStockRepository, IMissingMedicineLogRepository missingMedicineLogRepository, ILeaveDaysRequestRepository leaveDaysRequestRepository, IReviewRepository reviewRepository, IMedicinePurchaseRepository medicinePurchaseRepository, IRecipeRepository recipeRepository, IPromotionRepository promotionRepository, ISystemSettingsRepository systemSettingsRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.employmentContractRepository = employmentContractRepository;
        this.patientCategoryRepository = patientCategoryRepository;
        this.medicineRepository = medicineRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentPriceRepository = appointmentPriceRepository;
        this.orderRepository = orderRepository;
        this.offerRepository = offerRepository;
        this.medicineReservationRepository = medicineReservationRepository;
        this.medicineStockRepository = medicineStockRepository;
        this.missingMedicineLogRepository = missingMedicineLogRepository;
        this.leaveDaysRequestRepository = leaveDaysRequestRepository;
        this.reviewRepository = reviewRepository;
        this.medicinePurchaseRepository = medicinePurchaseRepository;
        this.recipeRepository = recipeRepository;
        this.promotionRepository = promotionRepository;
        this.systemSettingsRepository = systemSettingsRepository;
    }

    @Override
    @Transactional
    @SuppressWarnings("unused")
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Starting database initialization...");

        // Create authorities
        var systemAdminAuthority = createAuthority("ROLE_SYSTEM_ADMIN");
        var pharmacyAdminAuthority = createAuthority("ROLE_PHARMACY_ADMIN");
        var patientAuthority = createAuthority("ROLE_PATIENT");
        var pharmacistAuthority = createAuthority("ROLE_PHARMACIST");
        var dermatologistAuthority = createAuthority("ROLE_DERMATOLOGIST");
        var supplierAuthority = createAuthority("ROLE_SUPPLIER");

        // Create system admins
        var systemAdmin1 = new SystemAdmin("Stanko", "Antic", "stankoantic", TEST_123, "stankoantic@gmail.com", true, true);
        systemAdmin1.getAuthorities().add(systemAdminAuthority);
        var systemAdmin2 = new SystemAdmin("Pera", "Zivanovic", "perazivanovic", TEST_123, "perazivanovic@gmail.com", true, true);
        systemAdmin2.getAuthorities().add(systemAdminAuthority);
        var systemAdmin3 = new SystemAdmin(ADMIN, ADMIN, ADMIN, ADMIN, "admin@gmail.com", true, true);
        systemAdmin3.getAuthorities().add(systemAdminAuthority);
        userRepository.save(systemAdmin1);
        userRepository.save(systemAdmin2);
        userRepository.save(systemAdmin3);

        // Create patient categories
        var defaultCategory = createPatientCategory("Default category", 0, 0, "#ffffff");
        var bronzeCategory = createPatientCategory("Bronze", 1000, 3, "#632201");
        var silverCategory = createPatientCategory("Silver", 2000, 6, "#bfb9b6");
        var goldCategory = createPatientCategory("Gold", 3000, 9, "#eba502");
        var platinumCategory = createPatientCategory("Platinum", 4000, 15, "#14ffd4");


        // Create patients
        var p6 = createPatient("pera", "", 1650, 2, bronzeCategory, "0601133327", getNoviSadAddress("Gogoljeva", "14"), patientAuthority);
        var p1 = createPatient("Dejan", "Djordjevic", 1650, 1, bronzeCategory, "0601133327", getNoviSadAddress("Gogoljeva", "14"), patientAuthority);
        var p2 = createPatient("Ljiljana", "Petrovic", 2200, 1, silverCategory, "456", getNoviSadAddress("Radnicka", "88A"), patientAuthority);
        var p3 = createPatient("Pera", "Tanackovic", 3780, 1, goldCategory, "789", getNoviSadAddress("Sumadijska", "22"), patientAuthority);
        var p4 = createPatient("Ivana", "Mandic", 9000, 0, platinumCategory, "199", getNoviSadAddress("Resavska", "60"), patientAuthority);
        var p5 = createPatient("Pera", "Pera", 3600, 3, goldCategory, "199333111", getNoviSadAddress("Resavska", "62"), patientAuthority);

        var c1 = createComplaint("losa usluga", ComplaintType.PHARMACY, p1, "Benu Apoteka");
        var c2 = createComplaint("dugo sam cekao", ComplaintType.EMPLOYEE, p1, "Andrea Todorovic");
        var reply = new ComplaintReply("zao nam je", LocalDateTime.now(), systemAdmin1, c1);
        c1.setReply(reply);

        // Create pharmacies and their admins
        var benuLocation = new Location(45.25407418051719, 19.84837710688678, getNoviSadAddress("Bulevar Mihajla Pupina", "9"));
        var benu = new Pharmacy("Benu Apoteka", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer magna tortor, commodo elementum augue hendrerit, aliquet porttitor odio. Etiam efficitur pellentesque velit sit amet malesuada.", benuLocation);
        benu.setAverageGrade(4.86);
        benu.getComplaints().add(c1);
        var drMaxLocation = new Location(45.248661135597416, 19.839300607106516, getNoviSadAddress(BULEVAR_OSLOBODJENJA, "105"));
        var drMax = new Pharmacy("Dr Max", "Mauris et velit vitae justo aliquet aliquam tristique et risus. Nunc luctus elit at malesuada luctus. Aliquam tincidunt felis ac sodales bibendum.", drMaxLocation);
        var jankovicLocation = new Location(45.25681728629487, 19.81379914341574, getNoviSadAddress("Hadzi Ruvimovia", "48"));
        var jankovic = new Pharmacy("Jankovic", DESCRIPTION, jankovicLocation);
        var lillyLocation = new Location(45.26128371780874, 19.81573069750041, new Address(SERBIA, NOVI_SAD, "Janka Veselinovica", "20", "21137"));
        var lilly = new Pharmacy("Lilly", DESCRIPTION, lillyLocation);

        var dmLocation = new Location(45.26129717114014, 19.812867840128376, new Address(SERBIA, NOVI_SAD, "Trg Majke Jevrosime", "21", "21137"));
        var dm = new Pharmacy("DM", DESCRIPTION, dmLocation);

        var ibisLocation = new Location(45.2630180312307, 19.83046478671768, new Address(SERBIA, NOVI_SAD, BULEVAR_OSLOBODJENJA, "4a", ZIP_CODE));
        var ibis = new Pharmacy("Ibis", DESCRIPTION, ibisLocation);

        var mediGrupLocation = new Location(44.82198829616635, 20.462140149011613, new Address(SERBIA, "Stari Grad Urban Municipality", "Cara Dusana", "58", "11158"));
        var mediGrup = new Pharmacy("MediGrup", DESCRIPTION, mediGrupLocation);

        var apotekaBgLocation = new Location(44.82288751241458, 20.45877372595868, new Address(SERBIA, "Stari Grad Urban Municipality", "Kralja Petra", "85", "11158"));
        var apotekaBg = new Pharmacy("Apoteka Beograd", DESCRIPTION, apotekaBgLocation);

        var tiliaLocation = new Location(45.254689574828035, 19.8350150917808, new Address(SERBIA, NOVI_SAD, BULEVAR_OSLOBODJENJA, "66", ZIP_CODE));
        var tilia = new Pharmacy("Tilia", DESCRIPTION, tiliaLocation);

        var livsaneLocation = new Location(45.25001916978874, 19.848136592714777, new Address(SERBIA, NOVI_SAD, "Strazilovska", "19a", ZIP_CODE));
        var livsane = new Pharmacy("Livsane", DESCRIPTION, livsaneLocation);

        var treccaLocation = new Location(41.890558171850785, 12.506332260870309, new Address("Italy", "Rome", "Via Emanuele Filiberto", "155", "00185"));
        var trecca = new Pharmacy("Trecca Mastrangelli", DESCRIPTION, treccaLocation);

        var benuAdmin = new PharmacyAdmin("Vidoje", "Gavrilovic", "vidojegavrilovic", TEST_123, "vidojegavrilovic@gmail.com", true, true, benu);
        benuAdmin.getAuthorities().add(pharmacyAdminAuthority);
        benu.getPharmacyAdmins().add(benuAdmin);
        var drMaxAdmin = new PharmacyAdmin("Mladen", "Gojkovic", "mladengojkovic", TEST_123, "mladengojkovic@gmail.com", true, true, drMax);
        drMaxAdmin.getAuthorities().add(pharmacyAdminAuthority);
        drMax.getPharmacyAdmins().add(drMaxAdmin);
        var jankovicAdmin = new PharmacyAdmin("Milovan", "Todorovic", "milovantodorovic", TEST_123, "milovantodorovic@gmail.com", true, true, jankovic);
        jankovicAdmin.getAuthorities().add(pharmacyAdminAuthority);
        jankovic.getPharmacyAdmins().add(jankovicAdmin);
        var lillyAdmin = new PharmacyAdmin("root", "root", "root", "root", "root@gmail.com", true, true, lilly);
        lillyAdmin.getAuthorities().add(pharmacyAdminAuthority);
        lilly.getPharmacyAdmins().add(lillyAdmin);
        pharmacyRepository.save(benu);
        pharmacyRepository.save(drMax);
        pharmacyRepository.save(jankovic);
        pharmacyRepository.save(lilly);
        pharmacyRepository.save(dm);
        pharmacyRepository.save(ibis);
        pharmacyRepository.save(mediGrup);
        pharmacyRepository.save(apotekaBg);
        pharmacyRepository.save(tilia);
        pharmacyRepository.save(livsane);
        pharmacyRepository.save(trecca);

        // Create pharmacists
        var ph1 = createPharmacyEmployee("Rakita", "Moldovan", EmployeeType.PHARMACIST, pharmacistAuthority, 4.2);
        var ph2 = createPharmacyEmployee(VESNA, "Janketic", EmployeeType.PHARMACIST, pharmacistAuthority, 3.7);
        var ph3 = createPharmacyEmployee("Ljubinka", "Pap", EmployeeType.PHARMACIST, pharmacistAuthority, 5.0);
        var ph4 = createPharmacyEmployee("Sara", "Velimirovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.8);
        var ph5 = createPharmacyEmployee("Neda", "Pejic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.1);
        var ph6 = createPharmacyEmployee("Mira", "Vasic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.7);

        // Only for testing
        var tempPharmacist1 = createPharmacyEmployee("Slavica", "Krstic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.7);
        var tempPharmacist2 = createPharmacyEmployee("Dragana", "Aleksic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.8);
        var tempPharmacist3 = createPharmacyEmployee(VESNA, "Nedeljkovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.95);
        var tempPharmacist4 = createPharmacyEmployee("Gorana", "Andric", EmployeeType.PHARMACIST, pharmacistAuthority, 4.2);
        var tempPharmacist5 = createPharmacyEmployee("Zorka", "Bojanic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.1);
        var tempPharmacist6 = createPharmacyEmployee("Elena", "Borisavljevic", EmployeeType.PHARMACIST, pharmacistAuthority, 3.7);
        var tempPharmacist7 = createPharmacyEmployee(ANDREA, "Jovanovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.6);
        var tempPharmacist8 = createPharmacyEmployee("Bogdana", "Markovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.6);
        var tempPharmacist9 = createPharmacyEmployee("Bogdana", "Darkovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.5);
        var tempPharmacist10 = createPharmacyEmployee("Renja", "Miljatovic", EmployeeType.PHARMACIST, pharmacistAuthority, 2.7);
        var tempPharmacist11 = createPharmacyEmployee("Sofija", "Brkic", EmployeeType.PHARMACIST, pharmacistAuthority, 3.7);
        var tempPharmacist12 = createPharmacyEmployee("Milena", "Vujic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.8);

        var tempDermatologist1 = createPharmacyEmployee(ANDREA, "Todorovic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.7, c2);
        var tempDermatologist2 = createPharmacyEmployee(ANDREA, "Novakovic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.8);
        var tempDermatologist3 = createPharmacyEmployee("Snezana", "Brdjanin", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.95);
        var tempDermatologist4 = createPharmacyEmployee("Mina", "Savicevic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.2);
        var tempDermatologist5 = createPharmacyEmployee("Milana", "Lazic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.1);
        var tempDermatologist6 = createPharmacyEmployee("Jelena", "Aleksic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 3.7);
        var tempDermatologist7 = createPharmacyEmployee("Radina", "Vladimirovic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.6);
        var tempDermatologist8 = createPharmacyEmployee("Djurica", "Pesic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.6);
        var tempDermatologist9 = createPharmacyEmployee("Stojanka", "Carapic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.5);
        var tempDermatologist10 = createPharmacyEmployee("Emilija", "Nikolic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 2.7);
        var tempDermatologist11 = createPharmacyEmployee("Jasna", "Pajic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 3.7);
        var tempDermatologist12 = createPharmacyEmployee(VESNA, "Evic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.8);

        // Create dermatologists
        var dm1 = createPharmacyEmployee("Divna", "Bojanic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 3.6);
        var dm2 = createPharmacyEmployee("Mirjana", "Filipovic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 5.0);
        var dm3 = createPharmacyEmployee("Anastasija", "Bojevic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.4);

        // Create employment contracts
        var ec1 = createEmploymentContract(dm3, benu, FROM_EMPLOYMENT_CONTRACT, getWorkingHours(FROM_WORKING_HOURS, "12:00"));
        var ec2 = createEmploymentContract(ph1, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));

        // ############## Testing contract ######################################
        var ec2a = createEmploymentContract(tempPharmacist1, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2b = createEmploymentContract(tempPharmacist2, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2c = createEmploymentContract(tempPharmacist3, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2d = createEmploymentContract(tempPharmacist4, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2e = createEmploymentContract(tempPharmacist5, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2f = createEmploymentContract(tempPharmacist6, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2g = createEmploymentContract(tempPharmacist7, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2h = createEmploymentContract(tempPharmacist8, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2i = createEmploymentContract(tempPharmacist9, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2j = createEmploymentContract(tempPharmacist10, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2k = createEmploymentContract(tempPharmacist11, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2l = createEmploymentContract(tempPharmacist12, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));

        var tempC = createEmploymentContract(tempDermatologist1, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours("18:00", "20:00"));
        var ec2aa = createEmploymentContract(tempDermatologist1, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2bb = createEmploymentContract(tempDermatologist2, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2cc = createEmploymentContract(tempDermatologist3, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2dd = createEmploymentContract(tempDermatologist4, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2ee = createEmploymentContract(tempDermatologist5, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2ff = createEmploymentContract(tempDermatologist6, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2gg = createEmploymentContract(tempDermatologist7, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2hh = createEmploymentContract(tempDermatologist8, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2ii = createEmploymentContract(tempDermatologist9, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2jj = createEmploymentContract(tempDermatologist10, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2kk = createEmploymentContract(tempDermatologist11, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec2ll = createEmploymentContract(tempDermatologist12, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        // ##################################################################################################################
        var ec3 = createEmploymentContract(ph2, benu, FROM_EMPLOYMENT_CONTRACT, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec4 = createEmploymentContract(dm1, benu, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, "14:00"));
        var ec5 = createEmploymentContract(dm1, drMax, FROM_EMPLOYMENT_CONTRACT, getWorkingHours("15:00", TO_WORKING_HOURS));
        var ec6 = createEmploymentContract(ph3, drMax, "23.03.2021.", getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec7 = createEmploymentContract(ph4, drMax, FROM_EMPLOYMENT_CONTRACT, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec8 = createEmploymentContract(dm2, drMax, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, "11:00"));
        var ec9 = createEmploymentContract(dm2, jankovic, "24.03.2021.", getWorkingHours("11:30", TO_WORKING_HOURS));
        var ec10 = createEmploymentContract(ph5, jankovic, FROM_EMPLOYMENT_CONTRACT, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec11 = createEmploymentContract(ph6, jankovic, FROM_EMPLOYMENT_CONTRACT_BEFORE, getWorkingHours(FROM_WORKING_HOURS, TO_WORKING_HOURS));
        var ec12 = createEmploymentContract(dm3, jankovic, "23.03.2021.", getWorkingHours("14:00", TO_WORKING_HOURS));

        // Create suppliers
        var testSupplier = new Supplier("Dusan", "Erdeljan", "dusanerdeljan", TEST_123, "dusanerdeljan99@gmail.com", true, true, "Moja kompanija");
        var testSupplier1 = new Supplier("Dusan", "Erdeljan", "dusanerdeljan1", TEST_123, "dusanerdeljan858@gmail.com", true, true, "Moja kompanija");
        testSupplier.getAuthorities().add(supplierAuthority);
        testSupplier1.getAuthorities().add(supplierAuthority);
        userRepository.save(testSupplier);
        userRepository.save(testSupplier1);
        var s1 = createSupplier("Đurađ", "Nedeljković", GALENIKA_AD, supplierAuthority);
        var s2 = createSupplier("Miša", "Jelić", "HEMOFARM AD", supplierAuthority);
        var s3 = createSupplier("Milić", "Zebić", "PHARMANOVA D.O.O.", supplierAuthority);
        var s4 = createSupplier("Jakov", "Matic", "FIRMA D.O.O.", supplierAuthority);



        // Create medicines
        var aspirin = createMedicine(new Medicine("MED_1", "Aspirin", MedicineShape.TABLET, MedicineType.ANTIHISTAMINE, "acetilsalicilna kiselina", "BAYER BITTERFELD GMBH", false, NO_ADDITIONAL_NOTES_MSG, 1));
        var brufen = createMedicine(new Medicine("MED_2", "Brufen", MedicineShape.SYRUP, MedicineType.ANESTHETIC, "ibuprofen", "ABBVIE S.R.L.", false, NO_ADDITIONAL_NOTES_MSG, 2));
        var hepalpan = createMedicine(new Medicine("MED_3", "Hepalpan", MedicineShape.GEL, MedicineType.ANESTHETIC, "heparin-natrijum", GALENIKA_AD, false, NO_ADDITIONAL_NOTES_MSG, 2));
        var galitifen = createMedicine(new Medicine("MED_4", "Galitifen", MedicineShape.SYRUP, MedicineType.ANESTHETIC, "ketotifen", GALENIKA_AD, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var itanem = createMedicine(new Medicine("MED_5", "Itanem", MedicineShape.SOLUTION, MedicineType.ANESTHETIC, "meropenem", GALENIKA_AD, false, NO_ADDITIONAL_NOTES_MSG, 2));
        var paravano = createMedicine(new Medicine("MED_6", "Paravano", MedicineShape.TABLET, MedicineType.ANESTHETIC, "rosuvastatin", "HEMOFARM AD", true, NO_ADDITIONAL_NOTES_MSG, 2));
        var soliphar = createMedicine(new Medicine("MED_7", "SoliPhar", MedicineShape.TABLET, MedicineType.ANESTHETIC, "solifenacin", "PHARMAS D.O.O.", true, NO_ADDITIONAL_NOTES_MSG, 2));
        var gabana = createMedicine(new Medicine("MED_8", "Gabana", MedicineShape.CAPSULE, MedicineType.ANTIBIOTIC, "pregabalin", "PHARMACEUTICALBALKANS DOO", true, NO_ADDITIONAL_NOTES_MSG, 2));
        var nebispes = createMedicine(new Medicine("MED_9", "Nebispes", MedicineShape.TABLET, MedicineType.ANTIHISTAMINE, "nebivolol", "PHARMANOVA D.O.O.", true, NO_ADDITIONAL_NOTES_MSG, 2));
        var tragal = createMedicine(new Medicine("MED_10", "Tragal", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sertralin", GALENIKA_AD, true, NO_ADDITIONAL_NOTES_MSG, 2));

        var m2 = createMedicine(new Medicine("MED_12", "Lijek2", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, false, NO_ADDITIONAL_NOTES_MSG, 2));
        var m3 = createMedicine(new Medicine("MED_13", "Lijek3", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var m1 = createMedicine(new Medicine("MED_11", "Lijek1", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, false, NO_ADDITIONAL_NOTES_MSG, 2));
        var m4 = createMedicine(new Medicine("MED_14", "Lijek4", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var m5 = createMedicine(new Medicine("MED_15", "Lijek5", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var m6 = createMedicine(new Medicine("MED_16", "Lijek6", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var m7 = createMedicine(new Medicine("MED_17", "Lijek7", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var m8 = createMedicine(new Medicine("MED_18", "Lijek8", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var m9 = createMedicine(new Medicine("MED_19", "Lijek9", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var m10 = createMedicine(new Medicine("MED_20", "Lijek10", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));
        var m11 = createMedicine(new Medicine("MED_21", "Lijek11", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, SASTAV, PROIZVODJAC, true, NO_ADDITIONAL_NOTES_MSG, 2));

        // Create medicine replacements
        configureMedicineReplacements(hepalpan, nebispes, tragal);
        configureMedicineReplacements(paravano, itanem, soliphar);
        configureMedicineReplacements(galitifen, brufen, gabana);
        configureMedicineReplacements(gabana, tragal, nebispes, itanem, aspirin);
        configureMedicineReplacements(tragal, hepalpan, gabana, soliphar, itanem);

        // Add patient allergies
        configurePatientAllergies(p1, hepalpan, galitifen);
        configurePatientAllergies(p2, tragal);
        configurePatientAllergies(p3, soliphar, paravano);
        configurePatientAllergies(p4, nebispes);

        // Add medicine stock info
        addToStock(benu, aspirin, 300, 500);
        addToStock(benu, brufen, 240, 400);
        addToStock(benu, hepalpan, 400, 29);
        addToStock(benu, galitifen, 130, 50);
        addToStock(benu, gabana, 500, 0);
        addToStock(benu, itanem, 500, 0);
        addToStock(benu, nebispes, 100, 100);

        addToStock(drMax, aspirin, 310, 500);
        addToStock(drMax, brufen, 220, 400);
        addToStock(drMax, itanem, 500, 10);
        addToStock(drMax, paravano, 200, 56);

        addToStock(jankovic, aspirin, 290, 500);
        addToStock(jankovic, brufen, 260, 400);
        addToStock(jankovic, soliphar, 400, 60);
        addToStock(jankovic, gabana, 370, 100);

        // Configure pharmacy appointment prices
        configurePharmacyAppointmentPrices(benu, 3000, 3000);
        configurePharmacyAppointmentPrices(drMax, 2800, 3200);
        configurePharmacyAppointmentPrices(jankovic, 3500, 2500);

        // Create available appointments
        var a1 = createAvailableAppointment("09.04.2021. 09:00", "09.04.2021. 11:30", 2800, tempC);
        var a2 = createAvailableAppointment("10.04.2021. 10:30", "10.04.2021. 11:15", 3000, tempC);

        var a3 = createAvailableAppointment("14.04.2021. 22:00", "14.04.2021. 23:30", 2900, tempC);
        var a4 = createAvailableAppointment("17.04.2021. 15:45", "17.04.2021. 16:20", 3100, tempC);

        var a5 = createAvailableAppointment("28.05.2021. 10:00", "28.05.2021. 10:30", 2950, ec2a);
        var a6 = createAvailableAppointment("29.03.2021. 12:10", "29.03.2021. 12:30", 3050, ec9);


        // Create leave days request
        var request1 = new LeaveDaysRequest(LocalDate.of(2021, 6, 10), LocalDate.of(2021, 6, 24), ec1.getPharmacyEmployee(), LeaveDaysRequestStatus.APPROVED);
        var request2 = new LeaveDaysRequest(LocalDate.of(2021, 7, 10), LocalDate.of(2021, 7, 24), ec1.getPharmacyEmployee(), LeaveDaysRequestStatus.PENDING);
        var request3 = new LeaveDaysRequest(LocalDate.of(2021, 10, 10), LocalDate.of(2021, 10, 24), ec1.getPharmacyEmployee(), LeaveDaysRequestStatus.REJECTED, new Rejection("Some rejection reason."));
        leaveDaysRequestRepository.save(request1);
        leaveDaysRequestRepository.save(request2);
        leaveDaysRequestRepository.save(request3);

        // overlapping appointments with appointment 1
        var a16 = createAvailableAppointment("28.03.2022. 09:15", "28.03.2022. 09:45", 2800, ec1);
        var a17 = createAvailableAppointment("28.03.2022. 08:15", "28.03.2022. 09:15", 2800, ec1);
        var a18 = createAvailableAppointment("28.03.2022. 08:15", "28.03.2022. 09:45", 2800, ec1);


        // Create appointments which already took place
        var a7 = createAppointmentWhichTookPlace("01.02.2021. 12:00", "01.02.2021. 12:30", 3000.0, tempC, p1, new Report(DIJAGNOSTIKA));
        var a8 = createAppointmentWhichTookPlace("01.02.2021. 16:00", "01.02.2021. 16:25", 2000.0, tempC, p2, new Report(DIJAGNOSTIKA));
        var a9 = createAppointmentWhichTookPlace("01.02.2021. 09:00", "01.02.2021. 09:50", 2500.0, ec2aa, p3, new Report(DIJAGNOSTIKA));
        var a15 = createAppointmentWhichTookPlace("01.02.2021. 10:00", "01.02.2021. 10:45", 3000.0, ec2aa, p1, new Report(DIJAGNOSTIKA));
        var a19 = createAppointmentWhichTookPlace("01.02.2020. 10:00", "01.02.2020. 10:45", 2000.0, ec2aa, p1, new Report(DIJAGNOSTIKA));
        var a20 = createAppointmentWhichTookPlace("04.03.2019. 11:00", "04.03.2019. 12:45", 1000.0, ec4, p1, new Report(DIJAGNOSTIKA));

        var a90 = createAppointmentWhichTookPlace("04.03.2019. 11:00", "04.03.2019. 12:45", 1000.0, ec2a, p1, new Report(DIJAGNOSTIKA));
        var a91 = createAppointmentWhichTookPlace("04.03.2020. 11:00", "04.03.2020. 13:45", 1200.0, ec2b, p1, new Report(DIJAGNOSTIKA));
        var a92 = createAppointmentWhichTookPlace("04.03.2021. 11:00", "04.03.2021. 18:45", 1300.0, ec2c, p1, new Report(DIJAGNOSTIKA));

        // Add missing medicine logs (this is not valid data and is only used for testing)
        ArrayList<Medicine> logMedicines = new ArrayList<>(List.of(aspirin, brufen, galitifen));
        IntStream.range(0, 500).forEach(i -> {
            Collections.shuffle(logMedicines);
            missingMedicineLogRepository.save(new MissingMedicineLog(LocalDateTime.now(), logMedicines.get(0), a7));
            missingMedicineLogRepository.save(new MissingMedicineLog(LocalDateTime.now(), logMedicines.get(1), a7));
            missingMedicineLogRepository.save(new MissingMedicineLog(LocalDateTime.now(), logMedicines.get(2), a7));
        });

        var a10 = createBookedAppointment("25.06.2021. 12:00", "25.06.2021. 12:30", 3000, tempC, p1);
        var a11 = createBookedAppointment("23.06.2021. 18:00", "23.06.2021. 18:30", 3000, tempC, p1);
        var a12 = createBookedAppointment("01.02.2022. 12:00", "01.02.2022. 12:30", 3000, tempC, p1);
        var a21 = createBookedAppointment("02.05.2021. 08:00", "02.05.2021. 23:30", 1999, tempC, p1);
        var a22 = createBookedAppointment("19.05.2021. 01:00", "19.05.2021. 22:30", 3500, ec2aa, p1);
        var a55 = createBookedAppointment("17.05.2021. 01:00", "17.05.2021. 22:30", 3500, ec2aa, p2);
        var a23 = createBookedAppointment("21.04.2021. 09:40", "21.04.2021. 12:30", 2500, ec2a, p1);
        var a24 = createBookedAppointment("21.04.2021. 09:43", "21.04.2021. 12:45", 4500, ec2aa, p1);
        var a13 = createBookedAppointment("01.02.2021. 12:00", "01.02.2021. 12:30", 3000, ec2, p1);

        // Create pharmacist appointments 21.06.2021 5 per day overlapping 3 pharmacies
        var a25 = createAvailableAppointment(DATE_7, DATE_8,3000,ec1);
        var a26 = createAvailableAppointment(DATE_8, DATE_9,3000,ec1);
        var a27 = createAvailableAppointment(DATE_7, DATE_8,3000,ec3);
        var a28 = createAvailableAppointment(DATE_8, DATE_9,3000,ec3);
        var a29 = createAvailableAppointment(DATE_7, DATE_8,2800,ec6);
        var a30 = createAvailableAppointment(DATE_8, DATE_9,2800,ec6);
        var a31 = createAvailableAppointment("08.05.2021. 09:40", "08.05.2021. 12:30",2800,ec2aa);
        var a32 = createAvailableAppointment(DATE_8, DATE_9,2800,ec7);
        var a33 = createAvailableAppointment(DATE_7, DATE_8,3500,ec10);
        var a34 = createAvailableAppointment(DATE_8, DATE_9,3500,ec10);

        // Create pharmacist appointments 22.06.2021 2 per day overlapping 1 pharmacy
        var a35 = createAvailableAppointment(DATE_10, DATE_11,3000,ec1);
        var a37 = createAvailableAppointment(DATE_10, DATE_11,3000,ec3);
        var a38 = createAvailableAppointment(DATE_11, "22.06.2021. 15:30",3000,ec3);

        // Create pharmacist appointments 22.06.2021 3 per day overlapping 2 pharmacies
        var a39 = createAvailableAppointment(DATE_3, DATE_2,2800,ec2aa);
        var a40 = createAvailableAppointment(DATE_2, DATE_6,2800,ec6);
        var a41 = createAvailableAppointment(DATE_3, DATE_2,2800,ec7);
        var a42 = createAvailableAppointment(DATE_2, DATE_6,2800,ec7);
        var a43 = createAvailableAppointment(DATE_3, DATE_2,3500,ec10);
        var a44 = createAvailableAppointment(DATE_2, DATE_6,3500,ec10);

        // Create pharmacist appointments 22.06.2021 3 per day overlapping 2 pharmacies that are already booked
        var a49 = createBookedAppointment(DATE_3, DATE_2,2800,ec2a, p1);
        var a50 = createBookedAppointment("08.05.2021. 02:30", "08.05.2021. 22:30",2800,ec2aa, p1);
        var a51 = createBookedAppointment(DATE_3, DATE_2,2800,ec2c, p3);
        var a52 = createBookedAppointment(DATE_2, DATE_6,2800,ec2d, p4);
        var a53 = createBookedAppointment(DATE_3, DATE_2,3500,ec2e, p5);
        var a54 = createBookedAppointment("23.06.2021. 13:30", DATE_6,3500,ec2f, p1);

        // Test data for charts
        var a71 = createAppointmentWhichTookPlace("01.04.2021. 12:00", "01.04.2021. 12:30", 3000.0, tempC, p1, new Report(DIJAGNOSTIKA));
        var a82 = createAppointmentWhichTookPlace("01.03.2021. 16:00", "01.03.2021. 16:25", 2000.0, tempC, p2, new Report(DIJAGNOSTIKA));
        var a73 = createAppointmentWhichTookPlace("01.04.2021. 12:00", "01.04.2021. 12:30", 3000.0, tempC, p1, new Report(DIJAGNOSTIKA));
        var a84 = createAppointmentWhichTookPlace("01.06.2021. 16:00", "01.06.2021. 16:25", 2000.0, tempC, p2, new Report(DIJAGNOSTIKA));
        var a75 = createAppointmentWhichTookPlace("01.07.2021. 12:00", "01.07.2021. 12:30", 3000.0, tempC, p1, new Report(DIJAGNOSTIKA));
        var a86 = createAppointmentWhichTookPlace("01.08.2021. 16:00", "01.08.2021. 16:25", 2000.0, tempC, p2, new Report(DIJAGNOSTIKA));

        // Create medicine reservations
        var medicineReservation1 = createMedicineReservation(450.0, "28.03.2021. 10:00", "05.08.2021. 10:00", benu, p1, new MedicineReservationItem(2, aspirin, 300.0));
        var medicineReservation2 = createMedicineReservation(550.0, "28.03.2021. 10:15", "05.06.2021. 10:00", drMax, p2, new MedicineReservationItem(3, brufen, 220.0));
        var medicineReservation3 = createMedicineReservation(650.0, "28.03.2021. 10:20", "05.06.2021. 10:00", jankovic, p3, new MedicineReservationItem(3, aspirin, 290.0));
        var medicineReservation4 = createMedicineReservation(650.0, DATE_4, "05.06.2020. 10:00", jankovic, p1, new MedicineReservationItem(3, aspirin, 290.0));

        var medicineReservation5 = createIssuedMedicineReservation(650.0, DATE_4, "05.04.2020. 10:00", jankovic, p1, new MedicineReservationItem(3, aspirin, 290.0));
        var medicineReservation6 = createIssuedMedicineReservation(850.0, "23.03.2020. 10:20", "05.06.2020. 10:00", jankovic, p1, new MedicineReservationItem(3, brufen, 390.0));
        var medicineReservation7 = createIssuedMedicineReservation(350.0, "21.03.2020. 10:20", "05.07.2020. 10:00", benu, p1, new MedicineReservationItem(3, aspirin, 540.0));
        var medicineReservation8 = createIssuedMedicineReservation(450.0, DATE_4, "05.04.2020. 10:00", jankovic, p1, new MedicineReservationItem(3, aspirin, 290.0));
        var medicineReservation9 = createIssuedMedicineReservation(250.0, "23.03.2020. 10:20", "05.11.2020. 10:00", jankovic, p1, new MedicineReservationItem(2, brufen, 390.0));
        var medicineReservation10 = createIssuedMedicineReservation(150.0, "21.03.2020. 10:20", "05.12.2020. 10:00", benu, p1, new MedicineReservationItem(1, hepalpan, 540.0));

        // Create order
        var order1 = createOrder("05.06.2021. 18:00", benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen));
        var order2 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(50, hepalpan), new MedicineOrderInfo(100, galitifen));
        var order21 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(55, hepalpan), new MedicineOrderInfo(101, galitifen));
        var order22 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(51, hepalpan), new MedicineOrderInfo(99, galitifen));
        var order23 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(52, hepalpan), new MedicineOrderInfo(98, galitifen));
        var order24 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(53, hepalpan), new MedicineOrderInfo(97, galitifen));
        var order25 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(54, hepalpan), new MedicineOrderInfo(96, galitifen));
        var order31 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(55, hepalpan), new MedicineOrderInfo(101, galitifen));
        var order32 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(51, hepalpan), new MedicineOrderInfo(99, galitifen));
        var order33 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(52, hepalpan), new MedicineOrderInfo(98, galitifen));
        var order34 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(53, hepalpan), new MedicineOrderInfo(97, galitifen));
        var order35 = createOrder(DATE_1, benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(54, hepalpan), new MedicineOrderInfo(96, galitifen));

        var order3 = createOrder("07.06.2021. 17:30", drMaxAdmin, drMax, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(45, aspirin), new MedicineOrderInfo(66, brufen));
        var order4 = createOrder("08.06.2021. 16:50", drMaxAdmin, drMax, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(71, itanem), new MedicineOrderInfo(17, paravano));

        var order5 = createOrder("09.06.2021. 23:00", jankovicAdmin, jankovic, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(11, aspirin), new MedicineOrderInfo(54, brufen));
        var order6 = createOrder("10.06.2021. 15:00", jankovicAdmin, jankovic, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(41, soliphar), new MedicineOrderInfo(64, gabana));

        // Create offers
        var offer1 = createOffer(10000.0, "02.06.2021. 17:00", s4, order1, OfferStatus.ACCEPTED);
        var offer11 = createOffer(12000.0, "01.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        var offer12 = createOffer(14000.0, "03.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        var offer13 = createOffer(8000.0, "05.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        var offer14 = createOffer(12500.0, DATE_5, s1, order1, OfferStatus.REJECTED);
        var offer15 = createOffer(9600.0, "01.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        var offer16 = createOffer(8000.0, "12.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        var offer17 = createOffer(11000.0, "22.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        var offer18 = createOffer(12000.0, DATE_5, s1, order1, OfferStatus.REJECTED);
        var offer19 = createOffer(11000.0, "09.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        var offer110 = createOffer(10500.0, "08.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        var offer111 = createOffer(12400.0, DATE_5, s1, order1, OfferStatus.REJECTED);


        var offer3 = createOffer(13000.0, "04.06.2021. 16:00", s4, order3, OfferStatus.REJECTED);

        var offer4 = createOffer(9000.0, "02.06.2021. 12:00", s1, order3, OfferStatus.PENDING);
        var offer5 = createOffer(8600.0, "04.06.2021. 11:00", s2, order4, OfferStatus.PENDING);
        var offer6 = createOffer(8000.0, "03.06.2021. 15:00", s3, order5, OfferStatus.PENDING);

        var testOrder = createOrder("16.04.2021. 14:00", benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS,
                new MedicineOrderInfo(54, hepalpan),
                new MedicineOrderInfo(22, gabana),
                new MedicineOrderInfo(96, paravano, true, 390.0));
        var testOffer1 = createOffer(10000.0, "20.04.2021. 16:00", testSupplier, testOrder, OfferStatus.PENDING);
        var testOffer2 = createOffer(11000.0, "21.04.2021. 16:00", testSupplier1, testOrder, OfferStatus.PENDING);
        var testOffer3 = createOffer(12000.0, "22.04.2021. 16:00", testSupplier1, testOrder, OfferStatus.PENDING);

        // Test leave days requests
        createPendingLeaveDaysRequest(ec2aa, LocalDate.of(2021, 6, 10), LocalDate.of(2021, 6, 22));
        createAvailableAppointment("18.06.2021. 10:00", "18.06.2021. 10:30", 500, ec2a);
        createAvailableAppointment("19.06.2021. 10:00", "19.06.2021. 10:30", 500, ec2a);
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 5, 10), LocalDate.of(2021, 5, 22));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 5, 23), LocalDate.of(2021, 6, 20));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 8, 10), LocalDate.of(2021, 8, 22));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 9, 10), LocalDate.of(2021, 9, 22));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 10, 10), LocalDate.of(2021, 10, 22));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 11, 10), LocalDate.of(2021, 11, 22));

        createPendingLeaveDaysRequest(ec2bb, LocalDate.of(2021, 4, 10), LocalDate.of(2021, 5, 22));
        createPendingLeaveDaysRequest(ec2bb, LocalDate.of(2021, 7, 10), LocalDate.of(2021, 7, 22));
        createPendingLeaveDaysRequest(ec2bb, LocalDate.of(2021, 8, 10), LocalDate.of(2021, 8, 22));
        createPendingLeaveDaysRequest(ec2bb, LocalDate.of(2021, 9, 10), LocalDate.of(2021, 9, 22));
        createPendingLeaveDaysRequest(ec2ff, LocalDate.of(2021, 10, 10), LocalDate.of(2021, 10, 22));
        createPendingLeaveDaysRequest(ec2gg, LocalDate.of(2021, 11, 10), LocalDate.of(2021, 11, 22));

        // Create test medicine purchases
        IntStream.rangeClosed(1, 30).forEach(day ->  medicinePurchaseRepository.save(new MedicinePurchase(ThreadLocalRandom.current().nextInt(0, 30 + 1), 200.0, benu, LocalDate.of(2021, 4, day), aspirin)));
        IntStream.rangeClosed(1, 30).forEach(day ->  medicinePurchaseRepository.save(new MedicinePurchase(ThreadLocalRandom.current().nextInt(0, 20 + 1), 200.0, benu, LocalDate.of(2021, 4, day), brufen)));
        IntStream.rangeClosed(1, 30).forEach(day ->  medicinePurchaseRepository.save(new MedicinePurchase(ThreadLocalRandom.current().nextInt(0, 10 + 1), 200.0, benu, LocalDate.of(2021, 4, day), gabana)));

        // Add promotions
        var promotion = new Promotion(benu, "Hajmo na nog hop, ublazite bol uz ibutop...Samo mu recite stop uz ibutop, I B U T O P :grimmacing:", LocalDate.of(2021, 5, 2), LocalDate.of(2021, 5, 14), PromotionStatus.ACTIVE);
        var item1 = new PromotionItem(promotion, aspirin, 50);
        item1.setPriceReduction(300.0);
        var item2 = new PromotionItem(promotion, brufen, 50);
        item2.setPriceReduction(240.0);
        promotion.getPromotionItems().addAll(List.of(item1, item2));
        promotionRepository.save(promotion);

        // Employee reviews
        var r1 = createEmployeeReview(dm1, p3, 4);
        var r2 = createEmployeeReview(dm1, p2, 3);

        // Configure subscriptions
        subscribe(benu, p1, p2, p3);
        subscribe(drMax, p2, p3, p4);
        subscribe(jankovic, p1, p2, p4);

        var recipe1 = createRecipe(LocalDateTime.of(2021, 6, 1, 20, 4), p1, benu, new RecipeMedicineInfo(2,2,aspirin,200.0), new RecipeMedicineInfo(6, 4, brufen, 160.0));
        var recipe2 = createRecipe(LocalDateTime.of(2021, 7, 14, 18, 30), p1, benu, new RecipeMedicineInfo(14, 1, aspirin, 160.0));


        var systemSettings = new SystemSettings(3, 2);
        this.systemSettingsRepository.save(systemSettings);

        logger.info("Database initialized.");
    }

    public Recipe createRecipe(LocalDateTime time, Patient patient, Pharmacy pharmacy, RecipeMedicineInfo... recipeMedicineInfos){
        var recipe = new Recipe(time, false, patient, pharmacy);
        Arrays.stream(recipeMedicineInfos).forEach(recipeMedicineInfo -> {
            recipe.getReservedMedicines().add(recipeMedicineInfo);
            recipe.setPrice(recipe.getPrice() + recipeMedicineInfo.getPrice() * recipeMedicineInfo.getQuantity());
            recipeMedicineInfo.setRecipe(recipe);
        });
        this.recipeRepository.save(recipe);
        return recipe;

    }

    public Review createEmployeeReview(PharmacyEmployee employee, Patient patient, Integer rating){
        var review = new Review(rating, LocalDate.now(), ReviewType.EMPLOYEE, patient);
        employee.getReviews().add(review);

        employee.setAverageGrade(employee.getReviews().parallelStream()
                .reduce(
                        0d, (accumRating, rev) -> accumRating + rev.getGrade(),
                        Double::sum) / employee.getReviews().size());

        this.userRepository.save(employee);
        return this.reviewRepository.save(review);
    }
    public Complaint createComplaint(String title, ComplaintType type, Patient patient, String entity){
        return new Complaint(title, LocalDateTime.now(), type, patient,entity);

    }

    public void createPendingLeaveDaysRequest(EmploymentContract employee, LocalDate from, LocalDate to) {
        var request = new LeaveDaysRequest(from, to, employee.getPharmacyEmployee(), LeaveDaysRequestStatus.PENDING);
        leaveDaysRequestRepository.save(request);
    }

    private MedicineReservation createMedicineReservation(Double price, String reservedAt, String reservationDeadline, Pharmacy pharmacy, Patient patient, MedicineReservationItem... medicineReservationItems) {
        final var formatter = DateTimeFormatter.ofPattern(FORMAT);
        var medicineReservation = new MedicineReservation(price, LocalDateTime.parse(reservedAt, formatter), LocalDateTime.parse(reservationDeadline, formatter), ReservationStatus.RESERVED, pharmacy, patient);
        Arrays.stream(medicineReservationItems).forEach(medicineReservationItem -> {
            medicineReservationItem.setReservation(medicineReservation);
            medicineReservation.getReservedMedicines().add(medicineReservationItem);
        });
        patient.getMedicineReservations().add(medicineReservation);
        medicineReservationRepository.save(medicineReservation);
        return medicineReservation;
    }

    private MedicineReservation createIssuedMedicineReservation(Double price, String reservedAt, String reservationDeadline, Pharmacy pharmacy, Patient patient, MedicineReservationItem... medicineReservationItems) {
        final var formatter = DateTimeFormatter.ofPattern(FORMAT);
        var medicineReservation = new MedicineReservation(price, LocalDateTime.parse(reservedAt, formatter), LocalDateTime.parse(reservationDeadline, formatter), ReservationStatus.PICKED, pharmacy, patient);
        Arrays.stream(medicineReservationItems).forEach(medicineReservationItem -> {
            medicineReservationItem.setReservation(medicineReservation);
            medicineReservation.getReservedMedicines().add(medicineReservationItem);
        });
        medicineReservationRepository.save(medicineReservation);
        return medicineReservation;
    }

    private Offer createOffer(Double totalPrice, String deliveryDueDate, Supplier supplier, Order order, OfferStatus status) {
        final var formatter = DateTimeFormatter.ofPattern(FORMAT);
        var offer = new Offer(totalPrice, LocalDateTime.parse(deliveryDueDate, formatter), status, supplier, order);
        order.getAvailableOffers().add(offer);
        supplier.getMyOffers().add(offer);
        offerRepository.save(offer);
        return offer;
    }

    private Order createOrder(String dueDate, PharmacyAdmin admin, Pharmacy pharmacy, OrderStatus status, MedicineOrderInfo... medicineOrderInfos) {
        final var formatter = DateTimeFormatter.ofPattern(FORMAT);
        var order = new Order(LocalDateTime.parse(dueDate, formatter), status, admin, pharmacy);
        Arrays.stream(medicineOrderInfos).forEach(medicineOrderInfo -> {
            medicineOrderInfo.setOrder(order);
            order.getOrderItems().add(medicineOrderInfo);
        });
        admin.getPharmacy().getOrders().add(order);
        admin.getMyOrders().add(order);
        orderRepository.save(order);
        return order;
    }

    private Supplier createSupplier(String firstName, String lastName, String company, Authority authority) {
        var username = String.format("%s%s", firstName.toLowerCase(), lastName.toLowerCase());
        var supplier = new Supplier(firstName, lastName, username, TEST_123, generateMail(username), true, true, company);
        supplier.getAuthorities().add(authority);
        userRepository.save(supplier);
        return supplier;
    }

    private void configurePharmacyAppointmentPrices(Pharmacy pharmacy, double pharmacistAppointmentPrice, double dermatologistAppointmentPrice) {
        final var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        var fromDate = LocalDate.parse(FROM_EMPLOYMENT_CONTRACT_BEFORE, formatter);
        var pharmacistPrice = new AppointmentPrice(pharmacistAppointmentPrice, fromDate, null, false, pharmacy, EmployeeType.PHARMACIST);
        var dermatologistPrice = new AppointmentPrice(dermatologistAppointmentPrice, fromDate, null, false, pharmacy, EmployeeType.DERMATOLOGIST);
        pharmacy.addPharmacistAppointmentPrice(pharmacistPrice);
        pharmacy.addDermatologistAppointmentPrice(dermatologistPrice);
        appointmentPriceRepository.save(pharmacistPrice);
        appointmentPriceRepository.save(dermatologistPrice);
    }

    private Appointment createBookedAppointment(String from, String to, double price, EmploymentContract contract, Patient patient) {
        return createAppointment(from, to, price, contract, patient, AppointmentStatus.BOOKED, null);
    }

    private Appointment createAvailableAppointment(String from, String to, double price, EmploymentContract contract) {
        return createAppointment(from, to, price, contract, null, AppointmentStatus.AVAILABLE, null);
    }

    private Appointment createAppointmentWhichTookPlace(String from, String to, double price, EmploymentContract contract, Patient patient, Report report) {
        return createAppointment(from, to, price, contract, patient, AppointmentStatus.TOOK_PLACE, report);
    }

    private Appointment createAppointment(String from, String to, double price, EmploymentContract contract, Patient patient, AppointmentStatus status, Report report) {
        final var formatter = DateTimeFormatter.ofPattern(FORMAT);
        var dateFrom = LocalDateTime.parse(from, formatter);
        var dateTo = LocalDateTime.parse(to, formatter);
        var appointment = new Appointment(dateFrom, dateTo, price, status, patient, contract, report);
        appointmentRepository.save(appointment);
        return appointment;
    }

    private void addToStock(Pharmacy pharmacy, Medicine medicine, double price, int quantity) {
        var stock = new MedicineStock(quantity, pharmacy, medicine);
        var priceTag = new StockPrice(price, false, stock);
        stock.addPriceTag(priceTag);
        pharmacy.getMedicineStocks().add(stock);
        medicineStockRepository.save(stock);
    }

    private void configurePatientAllergies(Patient patient, Medicine... allergicTo) {
        Arrays.stream(allergicTo).forEach(medicine -> patient.getAllergicTo().add(medicine));
        userRepository.save(patient);
    }

    private Medicine createMedicine(Medicine medicine) {
        medicineRepository.save(medicine);
        return medicine;
    }

    private void configureMedicineReplacements(Medicine originalMedicine, Medicine... medicineReplacements) {
        Arrays.stream(medicineReplacements).forEach(replacement -> originalMedicine.getReplacements().add(replacement));
        medicineRepository.save(originalMedicine);
    }

    private void subscribe(Pharmacy pharmacy, Patient... subscribers) {
        Arrays.stream(subscribers).forEach(subscriber -> {
            pharmacy.getPromotionSubscribers().add(subscriber);
            subscriber.getSubscribedTo().add(pharmacy);
        });
        pharmacyRepository.save(pharmacy);
    }

    private EmploymentContract createEmploymentContract(PharmacyEmployee employee, Pharmacy pharmacy, String from, Collection<WorkingDay> workingHours) {
        final var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        var contract = new EmploymentContract(LocalDate.parse(from, formatter), null, employee, pharmacy);
        employee.getContracts().add(contract);
        pharmacy.getEmployees().add(contract);
        workingHours.forEach(workingDay -> {
            workingDay.setEmployee(contract);
            contract.getWorkingHours().add(workingDay);
        });
        employmentContractRepository.save(contract);
        return contract;
    }

    private Patient createPatient(String firstName, String lastName, int numPoints, int numPenalties, PatientCategory category, String phoneNumber, Address address, Authority authority) {
        var username = String.format("%s%s", firstName.toLowerCase(), lastName.toLowerCase());
        var patient = new Patient(firstName, lastName, username, TEST_123, generateMail(username), true, true, numPoints, numPenalties, phoneNumber, category, address);
        patient.getAuthorities().add(authority);
        userRepository.save(patient);
        return patient;
    }

    private PharmacyEmployee createPharmacyEmployee(String firstName, String lastName, EmployeeType employeeType, Authority authority, Double averageGrade, Complaint... complaints) {
        var username = String.format("%s%s", firstName.toLowerCase(), lastName.toLowerCase());
        var employee = new PharmacyEmployee(firstName, lastName, username, TEST_123, generateMail(username), true, true, employeeType);
        employee.getAuthorities().add(authority);
        employee.setAverageGrade(averageGrade);
        Arrays.stream(complaints).forEach(complaint -> employee.getComplaints().add(complaint));

        userRepository.save(employee);
        return employee;
    }

    private Address getNoviSadAddress(String street, String streetNumber) {
        return new Address("Srbija", NOVI_SAD, street, streetNumber, "21000");
    }

    private Collection<WorkingDay> getWorkingHours(String fromTime, String toTime) {
        final var formatter = DateTimeFormatter.ofPattern("HH:mm");
        var beginTimestamp = LocalTime.parse(fromTime, formatter);
        var endTimestamp = LocalTime.parse(toTime, formatter);
        return List.of(
                new WorkingDay(DayOfWeek.MONDAY, beginTimestamp, endTimestamp),
                new WorkingDay(DayOfWeek.TUESDAY, beginTimestamp, endTimestamp),
                new WorkingDay(DayOfWeek.WEDNESDAY, beginTimestamp, endTimestamp),
                new WorkingDay(DayOfWeek.THURSDAY, beginTimestamp, endTimestamp),
                new WorkingDay(DayOfWeek.FRIDAY, beginTimestamp, endTimestamp)
        );
    }

    private PatientCategory createPatientCategory(String categoryName, int numPoints, int discount, String color) {
        var patientCategory = new PatientCategory(categoryName, numPoints, discount, color);
        patientCategoryRepository.save(patientCategory);
        return patientCategory;
    }

    private Authority createAuthority(String roleName) {
        var authority = new Authority(roleName);
        authorityRepository.save(authority);
        return authority;
    }

    private String generateMail(String username) {
        return String.format("%s@gmail.com", username);
    }
}

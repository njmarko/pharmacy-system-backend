package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.ComplaintType;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.enums.ReviewType;
import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.*;
import com.mrsisa.pharmacy.service.IPharmacyEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PharmacyEmployeeService extends JPAService<PharmacyEmployee> implements IPharmacyEmployeeService {
    public static final String DOES_NOT_EXIST_ENDING = " does not exist.";
    private final IPharmacyEmployeeRepository pharmacyEmployeeRepository;
    private final IAuthorityRepository authorityRepository;
    private final IEmploymentContractRepository employmentContractRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IUserRepository userRepository;
    private final IPatientRepository patientRepository;
    private final IReviewRepository reviewRepository;

    @Autowired
    public PharmacyEmployeeService(IPharmacyEmployeeRepository pharmacyEmployeeRepository,
                                   IAuthorityRepository authorityRepository,
                                   IEmploymentContractRepository employmentContractRepository,
                                   IAppointmentRepository appointmentRepository, IUserRepository userRepository,
                                   IPatientRepository patientRepository, IReviewRepository reviewRepository) {
        this.pharmacyEmployeeRepository = pharmacyEmployeeRepository;
        this.authorityRepository = authorityRepository;
        this.employmentContractRepository = employmentContractRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    protected JpaRepository<PharmacyEmployee, Long> getEntityRepository() {
        return pharmacyEmployeeRepository;
    }

    @Override
    public Page<PharmacyEmployee> getPharmacyPharmacists(Pharmacy pharmacy, String firstName, String lastName, Double gradeLow, Double gradeHigh, Pageable pageable) {
        String firstNameParam = "%" + firstName + "%";
        String lastNameParam = "%" + lastName + "%";
        return pharmacyEmployeeRepository.getPharmacyEmployeesOfTypeSearchFilter(pharmacy.getId(), EmployeeType.PHARMACIST, firstNameParam, lastNameParam, gradeLow, gradeHigh, pageable);
    }

    @Override
    public Page<PharmacyEmployee> getPharmacyDermatologists(Pharmacy pharmacy, String firstName, String lastName, Double gradeLow, Double gradeHigh, Pageable pageable) {
        String firstNameParam = "%" + firstName + "%";
        String lastNameParam = "%" + lastName + "%";
        return pharmacyEmployeeRepository.getPharmacyEmployeesOfTypeSearchFilter(pharmacy.getId(), EmployeeType.DERMATOLOGIST, firstNameParam, lastNameParam, gradeLow, gradeHigh, pageable);
    }

    @Override
    public PharmacyEmployee registerPharmacist(Pharmacy pharmacy, PharmacyEmployee pharmacyEmployee, Collection<WorkingDay> workingDays) {
        validatePharmacistData(pharmacyEmployee);
        var contract = new EmploymentContract(LocalDate.now(), null, pharmacyEmployee, pharmacy);
        workingDays.forEach(workingDay -> {
            workingDay.setEmployee(contract);
            contract.getWorkingHours().add(workingDay);
        });
        pharmacyEmployee.getContracts().add(contract);
        pharmacyEmployee.getAuthorities().add(authorityRepository.findByName("ROLE_PHARMACIST"));
        pharmacy.getEmployees().add(contract);
        pharmacyEmployeeRepository.save(pharmacyEmployee);
        return pharmacyEmployee;
    }

    @Override
    public PharmacyEmployee hireDermatologist(Pharmacy pharmacy, PharmacyEmployee dermatologist, Collection<WorkingDay> workingDays) {
        var contract = new EmploymentContract(LocalDate.now(), null, dermatologist, pharmacy);
        workingDays.forEach(workingDay -> {
            workingDay.setEmployee(contract);
            contract.getWorkingHours().add(workingDay);
        });
        validateContract(dermatologist, pharmacy, contract);
        dermatologist.getContracts().add(contract);
        pharmacy.getEmployees().add(contract);
        employmentContractRepository.save(contract);
        return dermatologist;
    }

    @Override
    public void firePharmacist(Pharmacy pharmacy, PharmacyEmployee pharmacist) {
        var contract = employmentContractRepository.getEmployeeContractWithPharmacy(pharmacist.getId(), pharmacy.getId(), EmployeeType.PHARMACIST).orElseThrow(() -> new BusinessException("Pharmacist does not have employment contract in a given pharmacy."));
        throwIfCantFireEmployee(contract);
        endEmploymentContract(contract);
        pharmacyEmployeeRepository.save(contract.getPharmacyEmployee());
    }

    @Override
    public void fireDermatologist(Pharmacy pharmacy, PharmacyEmployee dermatologist) {
        var contract = employmentContractRepository.getEmployeeContractWithPharmacy(dermatologist.getId(), pharmacy.getId(), EmployeeType.DERMATOLOGIST).orElseThrow(() -> new BusinessException("Dermatologist does not have employment contract in a given pharmacy."));
        throwIfCantFireEmployee(contract);
        endEmploymentContract(contract);
        employmentContractRepository.save(contract);
    }

    private void validatePharmacistData(PharmacyEmployee pharmacists) {
        var sameUsername = userRepository.findByUsername(pharmacists.getUsername());
        if (sameUsername != null) {
            throw new BusinessException("Username is already taken.");
        }
        userRepository.findByEmail(pharmacists.getEmail()).ifPresent(user -> {
            throw new BusinessException("Email already in use.");
        });
    }

    private void endEmploymentContract(EmploymentContract contract) {
        contract.setActive(false);
        contract.setEndDate(LocalDate.now());
        contract.getRequests().forEach(request -> request.setActive(false));
        contract.getBookedAppointments().forEach(appointment -> {
            if (appointment.getAppointmentStatus() == AppointmentStatus.AVAILABLE) {
                appointment.setActive(false);
            }
        });
    }

    private void throwIfCantFireEmployee(EmploymentContract employmentContract) {
        Set<Appointment> futureBookedAppointments = appointmentRepository.getBookedAppointmentsForContract(employmentContract.getId());
        if (!futureBookedAppointments.isEmpty()) {
            throw new BusinessException("Employee has booked appointments.");
        }
    }

    @Override
    public PharmacyEmployee findByUsername(String username) {
        return pharmacyEmployeeRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Pharmacy findActivePharmacyOfPharmacist(String username) {
        var pharmacyEmployee = findByUsername(username);
        if (pharmacyEmployee == null || pharmacyEmployee.getEmployeeType() != EmployeeType.PHARMACIST) {
            return null;
        }

        for (EmploymentContract ec : pharmacyEmployee.getContracts()) {
            if (ec.getEndDate() == null) {
                return ec.getPharmacy();
            }
        }

        return null;
    }

    @Override
    public PharmacyEmployee getDermatologistById(Long dermatologistId) {
        return pharmacyEmployeeRepository.findEmployeeByIdOfType(dermatologistId, EmployeeType.DERMATOLOGIST)
                .orElseThrow(() -> new NotFoundException("Cannot find dermatologist with id: " + dermatologistId));
    }

    @Override
    public PharmacyEmployee getPharmacistById(Long pharmacistId) {
        return pharmacyEmployeeRepository.findEmployeeByIdOfType(pharmacistId, EmployeeType.PHARMACIST)
                .orElseThrow(() -> new NotFoundException("Cannot find pharmacist with id: " + pharmacistId));
    }

    @Override
    public PharmacyEmployee getDermatologistByUsername(String username) {
        return pharmacyEmployeeRepository.findDermatologistByUsernameWithContracts(username)
                .orElseThrow(() -> new NotFoundException("Cannot find dermatologist with username: " + username));
    }

    @Override
    public PharmacyEmployee createDermatologist(String firstName, String lastName, String username, String password, String email) {
        var employee = new PharmacyEmployee(firstName, lastName, username, password, email, true, false, EmployeeType.DERMATOLOGIST);
        if (this.userRepository.findByUsername(username) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.");
        if (this.userRepository.findByEmail(email).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is taken.");
        employee.getAuthorities().add(this.authorityRepository.findByName("ROLE_DERMATOLOGIST"));
        this.save(employee);
        return employee;
    }

    @Override
    public List<Pharmacy> getPharmacyEmployeePharmacies(Long employeeId) {
        var pharmacyEmployee = get(employeeId);
        List<Pharmacy> pharmacies = new ArrayList<>();
        pharmacyEmployee.getContracts().forEach(employmentContract -> {
            if (Boolean.TRUE.equals(employmentContract.getActive())) {
                pharmacies.add(employmentContract.getPharmacy());
            }
        });

        return pharmacies;
    }

    @Override
    public List<EmploymentContract> getAllPharmacyEmployees(Pharmacy pharmacy) {
        return employmentContractRepository.getPharmacyEmployees(pharmacy.getId());
    }

    @Override
    public Page<PharmacyEmployee> getDermatologists(Long pharmacyId, String firstName, String lastName, Double gradeLow, Double gradeHigh, Pageable pageable) {
        String firstNameParam = "%" + firstName + "%";
        String lastNameParam = "%" + lastName + "%";
        return pharmacyEmployeeRepository.getPharmacyEmployeesOfTypeSearchFilter(pharmacyId, EmployeeType.DERMATOLOGIST, firstNameParam, lastNameParam, gradeLow, gradeHigh, pageable);
    }

    @Override
    public Page<PharmacyEmployee> getPharmacists(Long pharmacyId, String firstName, String lastName, Double gradeLow, Double gradeHigh, Pageable pageable) {
        String firstNameParam = "%" + firstName + "%";
        String lastNameParam = "%" + lastName + "%";
        return pharmacyEmployeeRepository.getPharmacyEmployeesOfTypeSearchFilter(pharmacyId, EmployeeType.PHARMACIST, firstNameParam, lastNameParam, gradeLow, gradeHigh, pageable);
    }

    @Override
    @Transactional(rollbackFor = ResponseStatusException.class)
    public void rateEmployee(Long patientId, Long employeeId, EmployeeType employeeType, Integer rating) {

        var patient = this.patientRepository.findActivePatientUnlocked(patientId, Boolean.TRUE);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + patientId + DOES_NOT_EXIST_ENDING);
        }

        // pessimistic lock
        var employee = this.pharmacyEmployeeRepository.findEmployeeByIdOfTypeUnlocked(employeeId, employeeType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        employeeType.toString() + DOES_NOT_EXIST_ENDING));

        if (this.appointmentRepository
                .checkIfPatientHasAppointmentWithEmployee(patientId, employeeId, AppointmentStatus.TOOK_PLACE) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient does not have any previously concluded" +
                    " appointments with the selected " + employeeType +
                    ". Therefore he cannot rate the " + employeeType + ".");
        }
        // in case user has already reviewed the employee
        var review = employee.getReviews().stream().filter(r -> r.getReviewer().getId().equals(patient.getId())).findFirst()
                // in case there is no existing review for the employee
                .orElse(new Review());

        review.setReviewer(patient);
        review.setGrade(rating);
        review.setReviewType(ReviewType.EMPLOYEE);
        review.setDatePosted(LocalDate.now());

        employee.getReviews().add(review);
        employee.setAverageGrade(employee.getReviews().parallelStream()
                .reduce(
                        0d, (accumRating, rev) -> accumRating + rev.getGrade(),
                        Double::sum) / employee.getReviews().size());

        this.reviewRepository.save(review);
    }

    public Complaint fileComplaint(Long employeeId, Patient patient, String content) {
        Optional<PharmacyEmployee> optionalPharmacyEmployee = this.pharmacyEmployeeRepository.getByIdWithComplaints(employeeId);
        if (optionalPharmacyEmployee.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee with id " + employeeId + DOES_NOT_EXIST_ENDING);
        var employee = optionalPharmacyEmployee.get();
        Long appointments = this.appointmentRepository.checkIfPatientHasAppointmentWithEmployee(patient.getId(),employeeId,  AppointmentStatus.TOOK_PLACE);
        if (appointments == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot file a complaint against an employee " + employee.getFirstName() + " " + employee.getLastName() + ".");
        var complaint = new Complaint(content, LocalDateTime.now(), ComplaintType.EMPLOYEE, patient, employee.getFirstName() + " " + employee.getLastName());
        employee.getComplaints().add(complaint);
        this.pharmacyEmployeeRepository.saveAndFlush(employee);
        return complaint;
    }

    @Override
    public Review getPatientReviewForEmployee(Long patientId, Long employeeId, EmployeeType employeeType) {
        PharmacyEmployee employee = this.pharmacyEmployeeRepository.findEmployeeByIdOfType(employeeId, employeeType)
                .orElse(null);
        if (employee == null) {
            return null;
        }
        return employee.getReviews().stream().filter(r -> r.getReviewer().getId().equals(patientId)).findFirst()
                // in case there is no existing review for the employee
                .orElse(null);
    }

    @Override
    public List<PharmacyEmployee> getAllEmployeesOfType(EmployeeType employeeType) {
        return pharmacyEmployeeRepository.getEmployeesOfTypeList(employeeType);
    }

    @Override
    public PharmacyEmployee updateEmployee(Long id, String firstName, String lastName) {
        var pharmacyEmployee = get(id);
        pharmacyEmployee.setFirstName(firstName);
        pharmacyEmployee.setLastName(lastName);
        return pharmacyEmployee;
    }

    private void validateContract(PharmacyEmployee dermatologist, Pharmacy pharmacy, EmploymentContract newContract) {
        throwIfAlreadyWorksInPharmacy(dermatologist, pharmacy);
        dermatologist.getContracts().stream().filter(BaseEntity::getActive).forEach(contract -> throwIfContractsOverlap(contract, newContract));
    }

    private void throwIfAlreadyWorksInPharmacy(PharmacyEmployee dermatologist, Pharmacy pharmacy) {
        dermatologist.getContracts().forEach(contract -> {
            if (Boolean.TRUE.equals(contract.getActive()) && contract.getPharmacy().getId().equals(pharmacy.getId())) {
                throw new BusinessException("Dermatologist already works in this pharmacy.");
            }
        });
    }

    private void throwIfContractsOverlap(EmploymentContract first, EmploymentContract second) {
        Map<DayOfWeek, WorkingDay> workingSchedule = new EnumMap<>(DayOfWeek.class);
        first.getWorkingHours().forEach(workingDay -> workingSchedule.put(workingDay.getDay(), workingDay));
        second.getWorkingHours().forEach(workingDay -> {
            if (workingSchedule.containsKey(workingDay.getDay())) {
                if (workingHoursOverlap(workingSchedule.get(workingDay.getDay()), workingDay)) {
                    throw new BusinessException("Working hours overlap.");
                }
            } else {
                workingSchedule.put(workingDay.getDay(), workingDay);
            }
        });
    }

    private boolean workingHoursOverlap(WorkingDay first, WorkingDay second) {
        return first.getFromHours().isBefore(second.getToHours()) && second.getFromHours().isBefore(first.getToHours());
    }
}

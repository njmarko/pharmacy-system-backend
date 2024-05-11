package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPharmacyEmployeeRepository extends JpaRepository<PharmacyEmployee, Long> {
    @Query(value = "select em.pharmacyEmployee from EmploymentContract em" +
            " where em.active=true and em.endDate is null and (:id is null or em.pharmacy.id=:id) and em.pharmacyEmployee.employeeType=:employeeType" +
            " and lower(em.pharmacyEmployee.firstName) like :firstName and lower(em.pharmacyEmployee.lastName) like :lastName" +
            " and em.pharmacyEmployee.averageGrade >= :gradeLow and em.pharmacyEmployee.averageGrade <= :gradeHigh",
            countQuery = "select count(em) from EmploymentContract em where em.active=true and em.endDate is null " +
                    "and (:id is null or em.pharmacy.id=:id) and em.pharmacyEmployee.employeeType=:employeeType " +
                    "and lower(em.pharmacyEmployee.firstName) like :firstName and lower(em.pharmacyEmployee.lastName) like :lastName " +
                    "and em.pharmacyEmployee.averageGrade >= :gradeLow and em.pharmacyEmployee.averageGrade <= :gradeHigh")
    Page<PharmacyEmployee> getPharmacyEmployeesOfTypeSearchFilter(@Param("id") Long pharmacyId,
                                                                  @Param("employeeType") EmployeeType employeeType,
                                                                  @Param("firstName") String firstName,
                                                                  @Param("lastName") String lastName,
                                                                  @Param("gradeLow") Double gradeLow,
                                                                  @Param("gradeHigh") Double gradeHigh,
                                                                  Pageable pageable);

    @Query("select distinct d from PharmacyEmployee d left join fetch d.contracts where d.username=:username and d.employeeType=1 and d.active=true")
    Optional<PharmacyEmployee> findDermatologistByUsernameWithContracts(@Param("username") String dermatologistUsername);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select e from PharmacyEmployee e where e.id=:id and e.employeeType=:employeeType and e.active=true")
    Optional<PharmacyEmployee> findEmployeeByIdOfType(@Param("id") Long id, @Param("employeeType") EmployeeType employeeType);

    @Query("select e from PharmacyEmployee e where e.id=:id and e.employeeType=:employeeType and e.active=true")
    Optional<PharmacyEmployee> findEmployeeByIdOfTypeUnlocked(@Param("id") Long id, @Param("employeeType") EmployeeType employeeType);

    @Query("select distinct p from PharmacyEmployee p left join fetch p.contracts where p.username=:username and p.active=true")
    Optional<PharmacyEmployee> findByUsername(@Param("username") String username);


    @Query("select distinct e from PharmacyEmployee e left join e.complaints where e.active = true and e.id = :id")
    Optional<PharmacyEmployee> getByIdWithComplaints(@Param("id") Long id);

    @Query("select e from PharmacyEmployee e where e.active=true and e.employeeType=:type")
    List<PharmacyEmployee> getEmployeesOfTypeList(@Param("type") EmployeeType employeeType);
}
